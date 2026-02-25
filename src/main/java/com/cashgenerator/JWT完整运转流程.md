# JWT完整运转流程 - 从前端登录到后续请求

## 一、登录阶段 - JWT Token生成流程

### 1. 前端发起登录请求

**前端操作**:
```javascript
// 前端点击登录按钮
POST /customer/api/v1/validateCustomerCredentials
Content-Type: application/json

{
  "loginId": "user123",
  "password": "password123",
  "storeId": "1",
  "cardNumber": "1234",
  "ipaddress": "192.168.1.100",
  "macaddress": "00:11:22:33:44:55",
  "fingerPrintHash": "false"
}
```

**请求头**:
- 无Authorization头（登录端点不需要JWT）

---

### 2. Spring Security过滤器链处理

**流程**:
```
请求 → Spring Security Filter Chain
     ↓
WebSecurityConfig检查端点
     ↓
/customer/api/v1/validateCustomerCredentials 在permitAll列表中
     ↓
跳过JwtTokenFilter（因为登录端点不需要认证）
     ↓
继续到Controller
```

**关键配置** (`WebSecurityConfig.java`):
```java
.requestMatchers(
    "/customer/api/v1/validateCustomerCredentials",  // 登录端点公开
    ...
)
.permitAll()
```

---

### 3. RequestProcessingInterceptor预处理

**执行位置**: `RequestProcessingInterceptor.preHandle()`

**操作**:
1. 清空MDC上下文
2. 记录请求开始时间
3. 提取请求头信息（如果有）:
    - `Authorization`: 提取Token（登录时通常为空）
    - `X-Store-Id`: 店铺ID
    - `X-Till-Id`: 收银台ID
    - `X-User-Name`: 用户名
4. 将信息放入MDC用于日志追踪

**代码**:
```java
String authorizationHeader = request.getHeader("Authorization");
if(StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.contains("Bearer")) {
    String appAuthToken = authorizationHeader.split(" ")[1];
    MDC.put("appAuthToken", appAuthToken);
}
```

---

### 4. CustomerLoginController接收请求

**执行位置**: `CustomerLoginController.validateCustomerCredentials()`

**操作**:
```java
@PostMapping(CUSTOMER_LOGIN_VALIDATE)  // 实际路径: /customer/api/v1/validateCustomerCredentials
@ResponseBody
public LoginBo validateCustomerCredentials(
    @NotNull @Valid @RequestBody LoginBo loginBo, 
    @RequestParam(defaultValue = "LOGIN_AUTHENTICATION") AuthenticationType authenticationType
) {
    logger.info("store id:::{} ipAddr::{}", loginBo.getStoreId(), loginBo.getIpaddress());
    session.setAttribute("storeId", loginBo.getStoreId());  // 设置Session（虽然使用STATELESS，但仍有Session对象）
    return customerLoginService.validateCustomerCredentials(loginBo, authenticationType);
}
```

---

### 5. CustomerLoginService验证凭据

**执行位置**: `CustomerLoginService.validateCustomerCredentials()`

**详细验证流程**:

#### 5.1 验证店铺信息
```java
Store store = customerLoginRepository.getDetailsByStoreId(Long.parseLong(loginBo.getStoreId()));
if (store != null) {
    // 设置店铺相关信息
    loginBo.setContainerName(store.getContainerName());
    loginBo.setIsIdleTimeoutEnabled(store.getIsIdleTimeoutEnabled());
    loginBo.setIdleTimeoutTimeInMin(store.getIdleTimeoutTimeInMin());
}
```

#### 5.2 根据IP白名单检查策略分支

**分支A: IP白名单检查开启** (`store.getIpWhiteListCheck() == "Y"`)
```
1. 根据loginId查找Login记录
2. 验证用户是否在白名单中（!dbLogin.getIpWhiteListEnabled()）
3. 验证密码（使用Salt和Hash）
4. 验证IP地址是否在白名单中
5. 验证MAC地址是否匹配
6. 如果全部通过，返回成功（但不生成Token，因为这是白名单模式）
```

**分支B: IP白名单检查关闭** (`store.getIpWhiteListCheck() != "Y"`) - **主要流程**
```
1. 根据cardNumber查找StoreUsers记录
2. 获取关联的Login记录
3. 验证密码（支持指纹验证或密码验证）
4. 验证MAC地址是否匹配
5. 如果验证通过，执行以下操作：
   - createActiveUsers() - 创建活跃用户记录
   - createTokenList() - **生成JWT Token** ⭐
   - validatedLoginDetails() - 设置登录状态
   - 调用外部API获取用户信息
```

---

### 6. 生成JWT Token - 核心步骤

**执行位置**: `CustomerLoginService.createTokenList()`

**详细步骤**:

#### 6.1 序列化LoginBo对象
```java
String jsonLoginBo = null;
try {
    jsonLoginBo = new ObjectMapper().writeValueAsString(loginBo);
    // 将LoginBo对象转换为JSON字符串，包含用户所有登录信息
} catch (JsonProcessingException e) {
    LOGGER.error("Error while converting object to JSON: {}", loginBo, e);
    throw new RuntimeException(e);
}
```

#### 6.2 生成customerAuthToken
```java
String customerAuthToken = jwtUtils.generateJwtToken(
    loginBo.getLoginName(),           // Subject: 用户登录名
    "customer_auth_token",           // Claim名称
    jsonLoginBo                       // Claim值: LoginBo的JSON字符串
);
```

**生成的Token结构**:
```json
{
  "sub": "user123",
  "customer_auth_token": "{\"loginId\":\"user123\",\"storeId\":\"1\",...}",
  "iat": 1704067200000,
  "exp": 1704153600000
}
```

#### 6.3 生成appAuthToken
```java
String appAuthToken = jwtUtils.generateJwtToken(
    loginBo.getLoginName(),           // Subject: 用户登录名
    "app_auth_token",                 // Claim名称
    "customer_services"              // Claim值: 固定应用标识
);
```

**生成的Token结构**:
```json
{
  "sub": "user123",
  "app_auth_token": "customer_services",
  "iat": 1704067200000,
  "exp": 1704153600000
}
```

#### 6.4 将appAuthToken放入MDC
```java
Map<String, String> contextMap = MDC.getCopyOfContextMap();
contextMap.put("appAuthToken", appAuthToken);
MDC.setContextMap(contextMap);
// 用于后续日志追踪
```

#### 6.5 生成UUID Token并保存到数据库
```java
token = UUID.randomUUID();  // 生成UUID作为内部Token标识

UserAuthenication authenication = new UserAuthenication();
authenication.setUserId(loginBo.getLoginId());
authenication.setToken(token.toString());           // UUID Token
authenication.setStoreId(Long.parseLong(loginBo.getStoreId()));
authenication.setValid(true);
userAuthenicationRepository.save(authenication);   // 保存到数据库
```

#### 6.6 设置LoginBo的Token信息
```java
loginBo.setToken(token.toString());                    // UUID Token
loginBo.setCustomerAuthToken(customerAuthToken);       // JWT Token 1
loginBo.setAppAuthToken(appAuthToken);                 // JWT Token 2
```

---

### 7. 返回响应给前端

**响应内容** (`LoginBo`对象):
```json
{
  "loginId": "user123",
  "storeId": "1",
  "status": true,
  "message": "OK",
  "token": "550e8400-e29b-41d4-a716-446655440000",  // UUID Token
  "customerAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT Token 1
  "appAuthToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",        // JWT Token 2
  "tillsBoList": [...],
  "loginUserInfo": {
    "loginName": "user123",
    "role": "CASHIER",
    "operations": ["SALE", "REFUND", ...]
  },
  "password": ""  // 已清空
}
```

---

### 8. 前端存储Token

**前端操作**:
```javascript
// 前端收到响应后
const response = await loginAPI(credentials);

// 存储Token到localStorage或内存
localStorage.setItem('customerAuthToken', response.customerAuthToken);
localStorage.setItem('appAuthToken', response.appAuthToken);
localStorage.setItem('uuidToken', response.token);

// 或者使用状态管理（如Redux/Vuex）
store.dispatch('setAuthTokens', {
  customerAuthToken: response.customerAuthToken,
  appAuthToken: response.appAuthToken,
  uuidToken: response.token
});
```

---

## 二、后续请求阶段 - JWT Token验证流程

### 1. 前端发起业务请求

**前端操作**:
```javascript
// 前端发起业务请求（例如：获取客户列表）
GET /customer/api/v1/customers
Headers: {
  "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // 使用appAuthToken或customerAuthToken
  "X-Store-Id": "1",
  "X-Till-Id": "2",
  "X-User-Name": "user123"
}
```

**注意**: 通常使用`appAuthToken`作为Authorization头，因为它是应用级别的Token。

---

### 2. Spring Security过滤器链处理

**流程**:
```
请求 → Spring Security Filter Chain
     ↓
WebSecurityConfig检查端点
     ↓
/customer/api/v1/customers 不在permitAll列表中
     ↓
需要认证 → 继续过滤器链
     ↓
UsernamePasswordAuthenticationFilter (不处理JWT)
     ↓
JwtTokenFilter (处理JWT) ⭐
```

---

### 3. RequestProcessingInterceptor预处理

**执行位置**: `RequestProcessingInterceptor.preHandle()`

**操作**:
```java
// 提取Authorization头
String authorizationHeader = request.getHeader("Authorization");
if(StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.contains("Bearer")) {
    String appAuthToken = authorizationHeader.split(" ")[1];
    MDC.put("appAuthToken", appAuthToken);  // 放入MDC用于日志追踪
}

// 提取其他请求头
MDC.put("storeId", request.getHeader("X-Store-Id"));
MDC.put("tillId", request.getHeader("X-Till-Id"));
MDC.put("userName", request.getHeader("X-User-Name"));
```

---

### 4. JwtTokenFilter验证Token

**执行位置**: `JwtTokenFilter.doFilterInternal()`

**详细验证流程**:

#### 4.1 检查Authorization头
```java
final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
    // 没有Token或格式不正确
    chain.doFilter(request, response);  // 继续过滤器链，但未认证
    return;  // Spring Security会在后续拒绝请求
}
```

#### 4.2 提取Token
```java
final String token = header.split(" ")[1].trim();
// 从 "Bearer <token>" 中提取token部分
```

#### 4.3 验证Token有效性
```java
if (!jwtUtils.validateJwtToken(token)) {
    // Token无效（过期、签名错误等）
    chain.doFilter(request, response);  // 继续过滤器链，但未认证
    return;  // Spring Security会在后续拒绝请求
}
```

**验证内容**:
- Token签名是否正确（使用`app.jwtSecret`）
- Token是否过期（检查`exp`声明）
- Token格式是否正确

#### 4.4 提取用户信息
```java
// 从Token中提取customer_auth_token Claim
LoginBo loginBo = jwtUtils.getClaim(token, "customer_auth_token", LoginBo.class);

// 从Token中提取app_auth_token Claim
String applicationName = jwtUtils.getClaim(token, "app_auth_token", String.class);
```

**注意**:
- 如果Token是`appAuthToken`，`loginBo`可能为null
- 如果Token是`customerAuthToken`，`loginBo`包含完整用户信息

#### 4.5 创建UserDetails对象
```java
UserDetails userDetails = null;

if(loginBo != null) {
    // 有用户登录信息，创建用户认证对象
    userDetails = new LoginUserBo(1L, loginBo.getLoginId());
} else {
    // 只有应用Token，创建应用认证对象
    userDetails = new LoginUserBo(-1L, applicationName);
}
```

#### 4.6 设置Spring Security认证上下文
```java
// 创建认证Token
UsernamePasswordAuthenticationToken authentication = 
    new UsernamePasswordAuthenticationToken(
        userDetails,           // 主体
        null,                  // 凭证（JWT不需要）
        userDetails == null ? 
            List.of() : userDetails.getAuthorities()  // 权限列表
    );

// 设置认证详情
authentication.setDetails(
    new WebAuthenticationDetailsSource().buildDetails(request)
);

// 设置到SecurityContext
SecurityContextHolder.getContext().setAuthentication(authentication);
```

**关键**: 此时请求已被标记为"已认证"，Spring Security会允许请求继续。

#### 4.7 继续过滤器链
```java
chain.doFilter(request, response);  // 继续到下一个过滤器或Controller
```

---

### 5. Spring Security授权检查

**执行位置**: `WebSecurityConfig`的授权规则

**检查**:
```java
.authorizeHttpRequests((authorize) -> authorize
    .requestMatchers(...公开端点...)
    .permitAll()
    .anyRequest()
    .authenticated()  // 检查SecurityContext是否有认证信息
)
```

**结果**:
- ✅ 如果`SecurityContext`中有认证信息 → 允许访问
- ❌ 如果没有认证信息 → 返回401 Unauthorized

---

### 6. Controller处理请求

**执行位置**: 具体的Controller方法

**获取用户信息**:
```java
@GetMapping("/customers")
public List<Customer> getCustomers() {
    // 方式1: 从SecurityContext获取
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    UserDetails userDetails = (UserDetails) auth.getPrincipal();
    String username = userDetails.getUsername();
    
    // 方式2: 从MDC获取（如果之前设置了）
    String storeId = MDC.get("storeId");
    String userName = MDC.get("userName");
    
    // 执行业务逻辑
    return customerService.getCustomers(storeId);
}
```

---

### 7. Service层使用Token信息

**示例**: `TransactionsService`中动态生成Token
```java
private String createtoken(String userId) {
    String jsonLoginBo = new ObjectMapper().writeValueAsString(userId);
    // 生成新的Token用于内部调用
    String customerAuthToken = jwtUtils.generateJwtToken(
        getUserId(), 
        "customer_auth_token", 
        jsonLoginBo
    );
    return customerAuthToken;
}
```

---

### 8. 返回响应

**响应内容**:
```json
{
  "data": [...],
  "status": "success"
}
```

---

## 三、登出阶段 - Token失效流程

### 1. 前端发起登出请求

**前端操作**:
```javascript
POST /customer/api/v1/logout
Headers: {
  "Authorization": "Bearer <appAuthToken>",
  "Content-Type": "application/json"
}
Body: {
  "loginId": "user123",
  "storeId": "1",
  "token": "550e8400-e29b-41d4-a716-446655440000"  // UUID Token
}
```

---

### 2. JWT验证（同后续请求流程）

**流程**:
- RequestProcessingInterceptor提取Token
- JwtTokenFilter验证Token
- 设置SecurityContext

---

### 3. CustomerLoginController处理登出

**执行位置**: `CustomerLoginController.invalidate()`

**操作**:
```java
@PostMapping(CUSTOMER_LOGOUT)
@ResponseBody
public boolean invalidate(@NotNull @Valid @RequestBody LoginBo loginBo) {
    String token = loginBo.getToken();  // UUID Token
    
    // 查找数据库中的认证记录
    UserAuthenication authenication = new UserAuthenication();
    authenication.setToken(token);
    authenication.setUserId(loginBo.getLoginId());
    authenication.setStoreId(Long.parseLong(loginBo.getStoreId()));
    
    List<UserAuthenication> usersAuthenication = 
        userAuthenicationService.getByToken(
            authenication.getToken(), 
            authenication.getUserId(), 
            authenication.getStoreId()
        );
    
    // 标记所有匹配的记录为无效
    for (UserAuthenication userAuthenication : usersAuthenication) {
        if (userAuthenication != null) {
            userAuthenication.setInvalidateDate(new Timestamp(new Date().getTime()));
            userAuthenication.setValid(false);  // 标记为无效
            userAuthenicationrepository.save(userAuthenication);
        }
    }
    
    session.invalidate();  // 使Session失效
    return true;
}
```

**注意**:
- JWT Token本身无法立即失效（因为是无状态的）
- 系统通过数据库记录来标记Token失效
- 如果需要在验证时检查Token是否失效，需要在`JwtTokenFilter`中查询数据库

---

### 4. 前端清除Token

**前端操作**:
```javascript
// 清除本地存储的Token
localStorage.removeItem('customerAuthToken');
localStorage.removeItem('appAuthToken');
localStorage.removeItem('uuidToken');

// 清除状态管理中的Token
store.dispatch('clearAuthTokens');
```

---

## 四、完整流程图

```
┌─────────────────────────────────────────────────────────────────┐
│                    登录阶段 - Token生成                          │
└─────────────────────────────────────────────────────────────────┘

前端点击登录
    ↓
POST /customer/api/v1/validateCustomerCredentials
    ↓
Spring Security Filter Chain
    ├─ WebSecurityConfig: 检查端点 → permitAll ✅
    └─ 跳过JwtTokenFilter（登录端点不需要认证）
    ↓
RequestProcessingInterceptor.preHandle()
    ├─ 提取请求头
    └─ 放入MDC
    ↓
CustomerLoginController.validateCustomerCredentials()
    ↓
CustomerLoginService.validateCustomerCredentials()
    ├─ 验证店铺
    ├─ 验证用户凭据
    ├─ 验证MAC地址
    └─ createTokenList() ⭐
        ├─ 序列化LoginBo → JSON
        ├─ 生成customerAuthToken (JWT)
        ├─ 生成appAuthToken (JWT)
        ├─ 生成UUID Token
        ├─ 保存到数据库 (UserAuthenication)
        └─ 设置到LoginBo
    ↓
返回LoginBo（包含两个JWT Token）
    ↓
前端存储Token到localStorage/内存


┌─────────────────────────────────────────────────────────────────┐
│                  后续请求阶段 - Token验证                         │
└─────────────────────────────────────────────────────────────────┘

前端发起业务请求
    ↓
GET /customer/api/v1/customers
Headers: Authorization: Bearer <appAuthToken>
    ↓
Spring Security Filter Chain
    ├─ WebSecurityConfig: 检查端点 → 需要认证 ⚠️
    └─ 继续过滤器链
    ↓
RequestProcessingInterceptor.preHandle()
    ├─ 提取Authorization头
    └─ 放入MDC
    ↓
JwtTokenFilter.doFilterInternal() ⭐
    ├─ 检查Authorization头格式
    ├─ 提取Token
    ├─ validateJwtToken() → 验证签名和过期时间
    ├─ getClaim() → 提取用户信息
    ├─ 创建UserDetails
    └─ 设置SecurityContext ✅
    ↓
Spring Security授权检查
    └─ SecurityContext有认证信息 → 允许访问 ✅
    ↓
Controller处理请求
    └─ 可以从SecurityContext或MDC获取用户信息
    ↓
Service执行业务逻辑
    ↓
返回响应


┌─────────────────────────────────────────────────────────────────┐
│                    登出阶段 - Token失效                           │
└─────────────────────────────────────────────────────────────────┘

前端发起登出请求
    ↓
POST /customer/api/v1/logout
Headers: Authorization: Bearer <appAuthToken>
Body: { token: <uuidToken> }
    ↓
JWT验证（同后续请求流程）
    ↓
CustomerLoginController.invalidate()
    ├─ 查找数据库中的UserAuthenication记录
    ├─ 设置valid = false
    ├─ 设置invalidateDate
    └─ 保存到数据库
    ↓
session.invalidate()
    ↓
返回true
    ↓
前端清除本地Token
```

---

## 五、关键时间点说明

### Token生命周期

1. **生成时间**: 登录成功时，在`createTokenList()`方法中
2. **有效期**: 24小时（86400000毫秒），配置在`application.properties`
3. **验证时间**: 每次请求时，在`JwtTokenFilter`中
4. **失效时间**: 登出时，在数据库中标记为无效

### Token存储位置

1. **前端**: localStorage、sessionStorage或内存
2. **后端数据库**: `UserAuthenication`表（存储UUID Token）
3. **后端内存**: MDC上下文（用于日志追踪）
4. **后端内存**: SecurityContext（用于请求认证）

---

## 六、安全注意事项

### 1. Token传输
- ✅ 使用HTTPS传输Token
- ✅ Token放在Authorization头，不在URL中
- ⚠️ Token存储在localStorage有XSS风险，建议使用httpOnly Cookie

### 2. Token验证
- ✅ 验证签名防止篡改
- ✅ 验证过期时间
- ⚠️ 未在验证时检查数据库中的失效状态（如果Token被撤销，仍可使用到过期）

### 3. Token刷新
- ❌ 当前实现没有Token刷新机制
- ⚠️ Token过期后需要重新登录

### 4. 密钥管理
- ⚠️ JWT密钥硬编码在配置文件中
- ✅ 建议使用环境变量或密钥管理服务

---

## 七、总结

### 登录流程关键点
1. 登录端点不需要JWT认证（在permitAll列表中）
2. 验证成功后生成两个JWT Token（customerAuthToken和appAuthToken）
3. 同时生成UUID Token并保存到数据库
4. 所有Token信息返回给前端

### 请求验证流程关键点
1. 所有非公开端点都需要JWT认证
2. JwtTokenFilter在UsernamePasswordAuthenticationFilter之后执行
3. Token验证成功后设置SecurityContext
4. Spring Security根据SecurityContext决定是否允许访问

### 登出流程关键点
1. 通过数据库标记Token失效（JWT本身无法立即失效）
2. 前端需要清除本地存储的Token
3. 如果需要在验证时检查失效状态，需要在JwtTokenFilter中查询数据库
