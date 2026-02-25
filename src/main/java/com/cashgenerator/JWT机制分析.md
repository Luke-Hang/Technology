# 项目JWT机制分析报告

> 📖 **详细流程文档**: 查看 [JWT完整运转流程.md](./JWT完整运转流程.md) 了解从前端登录到后续请求的完整JWT运转流程

## 一、概述

该项目使用JWT（JSON Web Token）作为身份认证机制，结合Spring Security实现无状态的用户认证和授权。JWT机制贯穿整个请求生命周期，从登录时的Token生成到请求拦截时的Token验证。

## 二、核心组件

### 1. JWT工具类 (JwtUtils)

**位置**: `com.qsl.retail.util.JwtUtils` (可能在`retail-shared`依赖中)

**主要方法**:
- `generateJwtToken(String subject, String claimName, String claimValue)`: 生成JWT Token
- `validateJwtToken(String token)`: 验证JWT Token有效性
- `getClaim(String token, String claimName, Class<T> clazz)`: 从Token中提取Claim信息

**配置参数** (application.properties):
```properties
app.jwtSecret=jwtSecrte!@$@%%383  # JWT签名密钥
app.jwtExpirationMs=86400000       # Token过期时间（24小时）
```

**依赖库**:
- `io.jsonwebtoken:jjwt:0.9.1` - JWT处理库

### 2. JWT过滤器 (JwtTokenFilter)

**位置**: `src/main/java/com/qsl/retail/interceptor/JwtTokenFilter.java`

**功能**:
- 拦截所有HTTP请求
- 从`Authorization`请求头中提取JWT Token（格式：`Bearer <token>`）
- 验证Token有效性
- 从Token中提取用户信息并设置到Spring Security上下文

**工作流程**:
```java
1. 检查Authorization请求头是否存在且以"Bearer "开头
2. 提取Token字符串
3. 调用jwtUtils.validateJwtToken()验证Token
4. 从Token中提取两个Claim：
   - "customer_auth_token" -> LoginBo对象（用户登录信息）
   - "app_auth_token" -> 应用名称字符串
5. 根据提取的信息创建UserDetails对象
6. 设置到SecurityContextHolder中，供后续请求使用
```

**关键代码片段**:
```java
// 提取Token
final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
if (StringUtils.isEmpty(header) || !header.startsWith("Bearer ")) {
    chain.doFilter(request, response);
    return;
}

// 验证Token
final String token = header.split(" ")[1].trim();
if (!jwtUtils.validateJwtToken(token)) {
    chain.doFilter(request, response);
    return;
}

// 提取用户信息
LoginBo loginBo = jwtUtils.getClaim(token, "customer_auth_token", LoginBo.class);
String applicationName = jwtUtils.getClaim(token, "app_auth_token", String.class);
```

### 3. Spring Security配置 (WebSecurityConfig)

**位置**: `src/main/java/com/qsl/retail/config/WebSecurityConfig.java`

**配置要点**:
- **无状态会话**: `SessionCreationPolicy.STATELESS` - 不使用Session，完全依赖JWT
- **CSRF禁用**: 因为使用JWT，不需要CSRF保护
- **过滤器链**: JwtTokenFilter在`UsernamePasswordAuthenticationFilter`之后执行
- **公开端点**: 以下端点不需要JWT认证：
    - Swagger相关端点 (`/swagger-ui/**`, `/api-docs/**`)
    - Actuator端点 (`/actuator/**`)
    - 登录相关端点 (`/authenticate/**`, `/customer/api/v1/validateCustomerCredentials`)
    - 其他公开API

**安全配置代码**:
```java
.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
.authorizeHttpRequests((authorize) -> authorize
    .requestMatchers(...公开端点...)
    .permitAll()
    .anyRequest()
    .authenticated()
)
.addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
```

### 4. 登录服务 (CustomerLoginService)

**位置**: `src/main/java/com/qsl/retail/service/CustomerLoginService.java`

**Token生成流程** (`createTokenList`方法):

1. **生成两个JWT Token**:
    - `customerAuthToken`: 包含用户登录信息（LoginBo的JSON序列化）
      ```java
      String customerAuthToken = jwtUtils.generateJwtToken(
          loginBo.getLoginName(), 
          "customer_auth_token", 
          jsonLoginBo
      );
      ```
    - `appAuthToken`: 包含应用标识（固定值"customer_services"）
      ```java
      String appAuthToken = jwtUtils.generateJwtToken(
          loginBo.getLoginName(), 
          "app_auth_token", 
          "customer_services"
      );
      ```

2. **保存Token到数据库**:
    - 创建`UserAuthenication`实体
    - 生成UUID作为内部Token标识
    - 保存到`userAuthenicationRepository`

3. **设置MDC上下文**:
    - 将`appAuthToken`放入MDC（Mapped Diagnostic Context）用于日志追踪

4. **返回Token给客户端**:
    - `loginBo.setCustomerAuthToken(customerAuthToken)`
    - `loginBo.setAppAuthToken(appAuthToken)`

### 5. 请求处理拦截器 (RequestProcessingInterceptor)

**位置**: `src/main/java/com/qsl/retail/interceptor/RequestProcessingInterceptor.java`

**功能**:
- 从请求头中提取JWT Token并放入MDC
- 提取其他请求头信息（storeId, tillId, userName）
- 用于日志追踪和上下文传递

**关键代码**:
```java
String authorizationHeader = request.getHeader("Authorization");
if(StringUtils.isNotBlank(authorizationHeader) && authorizationHeader.contains("Bearer")) {
    String appAuthToken = authorizationHeader.split(" ")[1];
    MDC.put("appAuthToken", appAuthToken);
}
```

### 6. 登出机制 (CustomerLoginController)

**位置**: `src/main/java/com/qsl/retail/controller/CustomerLoginController.java`

**登出流程**:
1. 接收登出请求，包含Token信息
2. 从数据库查找对应的`UserAuthenication`记录
3. 将记录标记为无效（`setValid(false)`）
4. 设置失效时间（`setInvalidateDate`）
5. 保存到数据库
6. 使Session失效

**注意**: 虽然使用JWT（无状态），但登出时仍需要数据库操作来标记Token失效。这意味着系统需要维护Token的黑名单或状态。

## 三、JWT Token结构

### Token类型

项目使用两种JWT Token：

1. **customerAuthToken** (客户认证Token)
    - Claim名称: `"customer_auth_token"`
    - Claim值: `LoginBo`对象的JSON序列化字符串
    - 用途: 包含完整的用户登录信息

2. **appAuthToken** (应用认证Token)
    - Claim名称: `"app_auth_token"`
    - Claim值: `"customer_services"` (固定字符串)
    - 用途: 标识应用来源

### Token内容示例

```json
{
  "sub": "用户登录名",
  "customer_auth_token": "{LoginBo的JSON}",
  "app_auth_token": "customer_services",
  "iat": 时间戳,
  "exp": 过期时间戳
}
```

## 四、请求认证流程

```
1. 客户端请求
   ↓
2. RequestProcessingInterceptor
   - 提取Authorization头
   - 放入MDC上下文
   ↓
3. JwtTokenFilter
   - 检查Authorization头格式
   - 提取Token
   - 验证Token有效性
   - 提取用户信息
   - 设置SecurityContext
   ↓
4. Spring Security
   - 检查SecurityContext中的认证信息
   - 决定是否允许访问
   ↓
5. Controller/Service
   - 从SecurityContext获取用户信息
   - 执行业务逻辑
```

## 五、Token使用场景

### 1. 登录场景
- 用户通过`/customer/api/v1/validateCustomerCredentials`登录
- 系统验证凭据后生成两个JWT Token
- Token返回给客户端，客户端后续请求需携带Token

### 2. 业务服务场景
多个Service类在需要时动态生成Token：
- `TransactionsService`
- `ReserveItService`
- `TransactionsTakenService`
- `RefundAndExchangeService`
- `PaymentsService`

这些服务通过`createtoken()`方法生成新的Token用于内部调用。

### 3. 登出场景
- 客户端调用登出接口
- 服务器将Token标记为无效（数据库操作）
- Session失效

## 六、安全特性

### 优点
1. **无状态**: 使用STATELESS会话，服务器不需要维护Session
2. **可扩展**: Token包含用户信息，适合分布式系统
3. **标准化**: 使用JWT标准，易于与其他系统集成
4. **过期控制**: Token有24小时过期时间

### 潜在问题
1. **Token撤销**: 登出需要数据库操作，无法立即使Token失效（除非在验证时检查数据库）
2. **Token刷新**: 未发现Token刷新机制，过期后需要重新登录
3. **密钥安全**: JWT密钥硬编码在配置文件中，建议使用环境变量或密钥管理服务
4. **Token大小**: `customerAuthToken`包含完整LoginBo对象，可能导致Token较大

## 七、配置建议

### 1. 密钥管理
```properties
# 建议使用环境变量
app.jwtSecret=${JWT_SECRET:jwtSecrte!@$@%%383}
```

### 2. 过期时间
```properties
# 可根据业务需求调整
app.jwtExpirationMs=${JWT_EXPIRATION_MS:86400000}  # 24小时
```

### 3. 公开端点管理
定期审查`WebSecurityConfig`中的公开端点列表，确保没有敏感端点被意外公开。

## 八、总结

该项目的JWT机制实现相对完整，主要特点：

1. ✅ **完整的认证流程**: 登录生成Token → 请求验证Token → 登出失效Token
2. ✅ **Spring Security集成**: 与Spring Security良好集成，使用过滤器链
3. ✅ **双Token机制**: 使用customerAuthToken和appAuthToken分离关注点
4. ✅ **日志追踪**: 通过MDC传递Token信息用于日志追踪
5. ⚠️ **Token撤销**: 依赖数据库操作，可能存在性能问题
6. ⚠️ **密钥管理**: 建议改进密钥存储方式

整体而言，该JWT实现符合常见的最佳实践，但在Token撤销和密钥管理方面有改进空间。
