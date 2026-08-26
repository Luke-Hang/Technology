DROP DATABASE IF EXISTS explain_lab;
CREATE DATABASE explain_lab DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE explain_lab;

DROP PROCEDURE IF EXISTS seed_explain_lab;

DROP TABLE IF EXISTS customers;
CREATE TABLE customers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '客户主键',
  name VARCHAR(60) NOT NULL COMMENT '客户姓名，普通展示字段',
  email VARCHAR(120) NOT NULL COMMENT '客户邮箱，用于 LIKE 和普通索引练习',
  city VARCHAR(40) NOT NULL COMMENT '城市，普通二级索引练习点',
  status TINYINT NOT NULL COMMENT '客户状态：1=正常，0=停用；用于低选择性索引观察',
  created_at DATETIME NOT NULL COMMENT '创建时间，用于范围查询和联合索引练习'
) ENGINE=InnoDB COMMENT='客户表：用于练习普通索引、LIKE 查询和覆盖索引';

DROP TABLE IF EXISTS products;
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品主键',
  sku VARCHAR(40) NOT NULL COMMENT '商品 SKU，唯一索引练习点',
  category_id INT NOT NULL COMMENT '商品分类 ID，用于联合索引最左前缀练习',
  brand VARCHAR(40) NOT NULL COMMENT '品牌，未单独建索引，可观察过滤效果',
  price DECIMAL(10,2) NOT NULL COMMENT '价格，用于范围查询练习',
  stock INT NOT NULL COMMENT '库存数量，普通数据字段',
  status TINYINT NOT NULL COMMENT '商品状态：1=上架，0=下架',
  created_at DATETIME NOT NULL COMMENT '创建时间，用于状态加时间的联合索引练习',
  UNIQUE KEY uk_products_sku (sku)
) ENGINE=InnoDB COMMENT='商品表：用于练习唯一索引、联合索引、范围查询和选择性';

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单主键',
  order_no VARCHAR(40) NOT NULL COMMENT '订单号，唯一索引练习点',
  customer_id BIGINT NOT NULL COMMENT '客户 ID，用于订单按客户查询和 JOIN 练习',
  store_id INT NOT NULL COMMENT '门店 ID，用于联合索引过滤练习',
  status TINYINT NOT NULL COMMENT '订单状态：0=取消，1=待支付，2=处理中，3=完成',
  total_amount DECIMAL(12,2) NOT NULL COMMENT '订单金额，用于范围查询和后续加索引对比',
  created_at DATETIME NOT NULL COMMENT '下单时间，用于范围查询、排序和函数导致索引失效练习',
  paid_at DATETIME NULL COMMENT '支付时间，未支付订单为空',
  UNIQUE KEY uk_orders_order_no (order_no)
) ENGINE=InnoDB COMMENT='订单表：核心练习表，用于 EXPLAIN、联合索引、排序、范围查询和 JOIN 优化';

DROP TABLE IF EXISTS order_items;
CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单明细主键',
  order_id BIGINT NOT NULL COMMENT '订单 ID，用于从订单 JOIN 明细',
  product_id BIGINT NOT NULL COMMENT '商品 ID，用于按商品查询销售明细',
  quantity INT NOT NULL COMMENT '购买数量',
  unit_price DECIMAL(10,2) NOT NULL COMMENT '成交单价',
  created_at DATETIME NOT NULL COMMENT '明细创建时间，用于商品加时间的联合索引练习'
) ENGINE=InnoDB COMMENT='订单明细表：用于练习一对多 JOIN、连接字段索引和商品维度查询';

DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '支付记录主键',
  order_id BIGINT NOT NULL COMMENT '订单 ID，用于订单和支付记录 JOIN',
  pay_method VARCHAR(20) NOT NULL COMMENT '支付方式，如 CARD、CASH、PAYPAL 等',
  amount DECIMAL(12,2) NOT NULL COMMENT '支付金额',
  status TINYINT NOT NULL COMMENT '支付状态：1=成功，0=失败',
  created_at DATETIME NOT NULL COMMENT '支付创建时间，用于支付方式加时间的联合索引练习'
) ENGINE=InnoDB COMMENT='支付表：用于练习 LEFT JOIN、连接索引和支付方式维度查询';

DELIMITER //

CREATE PROCEDURE seed_explain_lab()
BEGIN
  DECLARE i INT DEFAULT 1;
  DECLARE order_id BIGINT DEFAULT 1;
  DECLARE item_count INT DEFAULT 0;
  DECLARE j INT DEFAULT 1;

  WHILE i <= 20000 DO
    INSERT INTO customers(name, email, city, status, created_at)
    VALUES (
      CONCAT('customer_', i),
      CONCAT('customer_', i, '@demo.local'),
      ELT(1 + MOD(i, 10), 'London', 'Manchester', 'Birmingham', 'Leeds', 'Glasgow', 'Liverpool', 'Bristol', 'Oxford', 'Cambridge', 'Cardiff'),
      IF(MOD(i, 20) = 0, 0, 1),
      TIMESTAMP('2023-01-01') + INTERVAL MOD(i, 900) DAY
    );
    SET i = i + 1;
  END WHILE;

  SET i = 1;
  WHILE i <= 5000 DO
    INSERT INTO products(sku, category_id, brand, price, stock, status, created_at)
    VALUES (
      CONCAT('SKU-', LPAD(i, 6, '0')),
      1 + MOD(i, 80),
      ELT(1 + MOD(i, 12), 'Sony', 'Apple', 'Dell', 'HP', 'Lenovo', 'Samsung', 'Xiaomi', 'Canon', 'Nike', 'Adidas', 'Bosch', 'LG'),
      10 + MOD(i * 37, 3000) + (MOD(i, 99) / 100),
      MOD(i * 13, 500),
      IF(MOD(i, 25) = 0, 0, 1),
      TIMESTAMP('2023-01-01') + INTERVAL MOD(i, 800) DAY
    );
    SET i = i + 1;
  END WHILE;

  SET i = 1;
  WHILE i <= 100000 DO
    INSERT INTO orders(order_no, customer_id, store_id, status, total_amount, created_at, paid_at)
    VALUES (
      CONCAT('ORD-', LPAD(i, 8, '0')),
      1 + MOD(i * 17, 20000),
      1 + MOD(i, 60),
      CASE MOD(i, 10) WHEN 0 THEN 0 WHEN 1 THEN 1 WHEN 2 THEN 2 ELSE 3 END,
      20 + MOD(i * 97, 50000) / 10,
      TIMESTAMP('2024-01-01') + INTERVAL MOD(i, 700) DAY + INTERVAL MOD(i, 86400) SECOND,
      IF(MOD(i, 10) IN (0, 1), NULL, TIMESTAMP('2024-01-01') + INTERVAL MOD(i, 700) DAY + INTERVAL MOD(i + 300, 86400) SECOND)
    );
    SET i = i + 1;
  END WHILE;

  SET order_id = 1;
  WHILE order_id <= 100000 DO
    SET item_count = 1 + MOD(order_id, 5);
    SET j = 1;
    WHILE j <= item_count DO
      INSERT INTO order_items(order_id, product_id, quantity, unit_price, created_at)
      VALUES (
        order_id,
        1 + MOD(order_id * j * 19, 5000),
        1 + MOD(order_id + j, 4),
        10 + MOD(order_id * j * 23, 3000) / 10,
        TIMESTAMP('2024-01-01') + INTERVAL MOD(order_id, 700) DAY
      );
      SET j = j + 1;
    END WHILE;
    SET order_id = order_id + 1;
  END WHILE;

  SET i = 1;
  WHILE i <= 95000 DO
    INSERT INTO payments(order_id, pay_method, amount, status, created_at)
    VALUES (
      i,
      ELT(1 + MOD(i, 5), 'CARD', 'CASH', 'PAYPAL', 'APPLE_PAY', 'BANK'),
      20 + MOD(i * 97, 50000) / 10,
      IF(MOD(i, 13) = 0, 0, 1),
      TIMESTAMP('2024-01-01') + INTERVAL MOD(i, 700) DAY + INTERVAL MOD(i + 600, 86400) SECOND
    );
    SET i = i + 1;
  END WHILE;
END
//

DELIMITER ;

CALL seed_explain_lab();
DROP PROCEDURE seed_explain_lab;

ANALYZE TABLE customers, products, orders, order_items, payments;

-- 基础检查：确认造数是否成功。后面的 EXPLAIN 要有足够数据量才容易看出差异。
SELECT COUNT(*) AS customers_count FROM customers;
SELECT COUNT(*) AS products_count FROM products;
SELECT COUNT(*) AS orders_count FROM orders;
SELECT COUNT(*) AS order_items_count FROM order_items;
SELECT COUNT(*) AS payments_count FROM payments;



索引的目的是：更快的找到数据

索引的本质是：创建并维护额外的数据结构(比如 B+Tree)，减少扫描范围，提高查询效率。
没有索引时,MySQL 可能需要扫描整张表，逐行判断条件，最终找出符合条件的记录。
例如:
SELECT * FROM orders WHERE customer_id = 100; 从 orders 表找出 customer_id = 100 的记录
没有索引时,MySQL 可能需要扫描整张表，逐行判断 customer_id 是否等于 100,最终找出 customer_id = 100 的记录。

给 customer_id 创建索引后,InnoDB 会创建并维护一棵以 customer_id 为索引键的 B+Tree。
再次查询时,MySQL 就可以先通过这棵 B+Tree 快速定位符合条件的数据，而不是扫描整张表。
例如：
CREATE INDEX idx_orders_customer_id ON orders(customer_id); 给 customer_id 创建索引后
再次执行 SELECT * FROM orders WHERE customer_id = 100; 再次从 orders 表找出 customer_id = 100 的记录
MySQL 就可以先通过这棵 B+Tree 快速定位符合条件的数据，而不是扫描整张表。

最常见的是优化 WHERE 条件查询，同时也可以优化 JOIN、ORDER BY、GROUP BY 等操作。


缺点：代价是会占用额外存储空间，并增加 INSERT / UPDATE / DELETE 时维护索引的成本。


-- SHOW INDEX FROM customers;
-- SHOW INDEX FROM orders;

-- 练习 0: 主键/唯一索引查询。
-- 作用：观察主键和唯一索引等值查询通常会变成 const，rows 通常很小。
-- 观察点：customers 使用 PRIMARY，orders 使用 uk_orders_order_no。
EXPLAIN SELECT * FROM customers WHERE id = 1;
EXPLAIN SELECT * FROM orders WHERE order_no = 'ORD-00000001';


-- 练习 1: 普通范围查询，索引不一定会被使用。
-- 作用：观察 possible_keys 和 key 的区别；possible_keys 有值只代表“可能用”，key 有值才代表“实际使用”。
-- 说明：total_amount > 4000 是普通范围查询，命中行数较多，并且 SELECT * 需要回表，MySQL 可能仍然选择全表扫描。
EXPLAIN SELECT * FROM orders WHERE total_amount > 4000;

SHOW INDEX FROM orders;
CREATE INDEX idx_orders_total_amount ON orders (total_amount);
-- DROP INDEX idx_orders_total_amount ON orders;

-- 观察点：加索引后，possible_keys 可能出现 idx_orders_total_amount，但 key 仍可能是 NULL，
-- 因为 total_amount > 4000 是范围查询且命中行数较多，SELECT * 回表成本可能较高。
EXPLAIN SELECT * FROM orders WHERE total_amount > 4000;

-- 观察点：条件范围更小时，索引更可能真正生效，type 更可能变成 range。
-- 说明：BETWEEN 是范围查询，B+Tree 索引可以用于范围定位；命中行数较少时，优化器更可能选择使用索引。
EXPLAIN SELECT * FROM orders WHERE total_amount BETWEEN 4900 AND 4910;

-- 覆盖索引:查询所需字段都在索引里，MySQL 可能不用再回表查整行数据，直接从索引里就能把结果拿出来,这就叫覆盖索引

-- [速记]
-- 覆盖索引 = 查询所需字段都在索引里 = 不需要回表查询。
-- 回表 = 索引里的字段不够，先通过二级索引找到主键，再根据主键去查整行数据。

-- 执行 SELECT * FROM orders WHERE customer_id = 100;
-- 普通二级索引里主要有:customer_id + 主键id
-- MySQL 会先通过 customer_id 索引找到:customer_id = 100,id = 123 这两个字段的值。
-- 然后再拿 id = 123 去主键索引里查整行数据，得到 total_amount,status,created_at 等字段的值
-- 这个“再根据主键去查整行数据”的过程，就叫回表。
-- 回表 = 二级索引 → 找到主键 → 主键索引 → 获取其他字段。
--
-- SELECT * FROM orders WHERE customer_id = 100
--         ↓
-- 查询 customer_id 二级索引
--         ↓
-- 找到 customer_id = 100,主键 id = 123
--         ↓
-- 拿 id = 123 查询主键（聚簇）索引
--         ↓
-- 得到 total_amount、status、created_at 等其他字段 这一步“根据主键再次查询完整数据”，就是回表。

-- 观察点：只查索引字段时更容易走覆盖索引，Extra 可能出现 Using index。
EXPLAIN SELECT total_amount FROM orders WHERE total_amount > 4000;

-- 练习 2: 联合索引的最左前缀。
-- 作用：理解联合索引 (store_id, status, created_at) 为什么适合 store_id + status + created_at 排序/范围场景。
-- 观察点：key 应该使用 idx_orders_store_status_created，type 通常是 ref/range，rows 会明显减少。
SHOW INDEX FROM orders;
CREATE INDEX idx_orders_store_status_created ON orders (store_id, status, created_at);
-- DROP INDEX idx_orders_store_status_created ON orders;

EXPLAIN SELECT * FROM orders
WHERE store_id = 8 AND status = 3
ORDER BY created_at DESC
LIMIT 20;

-- 练习 3: 不满足联合索引最左前缀。
-- 作用：理解只查 status 时，无法高效使用 (store_id, status, created_at)，因为跳过了最左列 store_id。
-- 观察点：加新索引前，可能出现 type=ALL、Using filesort 或扫描行数较大。
EXPLAIN SELECT * FROM orders
WHERE status = 3
ORDER BY created_at DESC
LIMIT 20;

SHOW INDEX FROM orders;
CREATE INDEX idx_orders_status_created ON orders (status, created_at);
-- DROP INDEX idx_orders_status_created ON orders;

-- 观察点：加 (status, created_at) 后，key 应该变成 idx_orders_status_created，排序也更容易利用索引。
EXPLAIN SELECT * FROM orders
WHERE status = 3
ORDER BY created_at DESC
LIMIT 20;

-- 练习 4: 函数导致索引难以利用。
-- 作用：理解不要在索引列上包函数；DATE(created_at) 会让 MySQL 难以直接按 idx_orders_created 做范围定位。
-- 观察点：第一条可能扫描更多行；第二条改成时间范围后，更容易使用 idx_orders_created。
EXPLAIN SELECT * FROM orders
WHERE created_at >= '2025-01-01'
  AND created_at < '2025-01-02';

SHOW INDEX FROM orders;
CREATE INDEX idx_orders_created ON orders (created_at);
-- DROP INDEX idx_orders_created ON orders;

EXPLAIN SELECT * FROM orders
WHERE DATE(created_at) = '2025-01-01';
EXPLAIN SELECT * FROM orders
WHERE created_at >= '2025-01-01'
  AND created_at < '2025-01-02';

-- 练习 5: 覆盖索引。
-- 作用：理解查询字段都在索引里时，MySQL 可以只读索引，不回表。
-- 观察点：idx_orders_customer_created 包含 customer_id 和 created_at，Extra 可能出现 Using index。
EXPLAIN SELECT customer_id, created_at FROM orders
WHERE customer_id = 123
ORDER BY created_at DESC
LIMIT 10;

SHOW INDEX FROM orders;
CREATE INDEX idx_orders_customer_created ON orders (customer_id, created_at);
-- DROP INDEX idx_orders_customer_created ON orders;

EXPLAIN SELECT customer_id, created_at FROM orders
WHERE customer_id = 123
ORDER BY created_at DESC
LIMIT 10;

-- 练习 6: JOIN 的驱动表和连接索引。
-- 作用：观察多表 JOIN 时，每张表各自使用哪个索引，以及 rows 是否合理。
-- 观察点：orders 应优先用 idx_orders_store_status_created 缩小范围；customers 用主键；payments 用 idx_payments_order。
SHOW INDEX FROM payments;
CREATE INDEX idx_payments_order ON payments (order_id);
-- DROP INDEX idx_payments_order ON payments;

EXPLAIN SELECT o.id, o.order_no, c.name, p.pay_method
FROM orders o
JOIN customers c ON c.id = o.customer_id
LEFT JOIN payments p ON p.order_id = o.id
WHERE o.store_id = 8
  AND o.status = 3
  AND o.created_at >= '2025-01-01'
  AND o.created_at < '2025-02-01'
ORDER BY o.created_at DESC
LIMIT 50;

-- 练习 7: LIKE 前缀匹配和前置通配符。
-- 作用：理解 'abc%' 可以利用 B+Tree 索引范围扫描，而 '%abc' 通常无法高效利用普通索引。
-- 观察点：加 email 索引后，'customer\_12%' 更可能使用索引范围扫描，'%12@demo.local' 通常仍然难以高效利用普通 B+Tree 索引。
EXPLAIN SELECT * FROM customers WHERE email LIKE 'customer\_12%';
EXPLAIN SELECT * FROM customers WHERE email LIKE '%12@demo.local';

SHOW INDEX FROM customers;
CREATE INDEX idx_customers_email ON customers (email);
-- DROP INDEX idx_customers_email ON customers;

EXPLAIN SELECT * FROM customers WHERE email LIKE 'customer\_12%';
EXPLAIN SELECT * FROM customers WHERE email LIKE '%12@demo.local';

-- 练习 8: GROUP BY 优化。
-- 作用：观察 GROUP BY 在没有合适索引时，可能出现 Using temporary / Using filesort。
-- 观察点：加索引前后对比 key、rows、Extra，理解索引列顺序会影响过滤和分组成本。
EXPLAIN SELECT store_id, status, COUNT(*), SUM(total_amount)
FROM orders
WHERE created_at >= '2025-01-01'
GROUP BY store_id, status;

SHOW INDEX FROM orders;
CREATE INDEX idx_orders_created_store_status ON orders (created_at, store_id, status);
-- DROP INDEX idx_orders_created_store_status ON orders;

EXPLAIN SELECT store_id, status, COUNT(*), SUM(total_amount)
FROM orders
WHERE created_at >= '2025-01-01'
GROUP BY store_id, status;

-- 练习 9: ORDER BY 与 filesort。
-- 作用：观察没有合适索引时，WHERE + ORDER BY 可能出现 Using filesort。
-- 观察点：加 (pay_method, created_at) 后，过滤和排序更容易同时利用索引。
EXPLAIN SELECT * FROM payments
WHERE pay_method = 'CARD'
ORDER BY created_at DESC
LIMIT 20;

SHOW INDEX FROM payments;
CREATE INDEX idx_payments_method_created ON payments (pay_method, created_at);
-- DROP INDEX idx_payments_method_created ON payments;

EXPLAIN SELECT * FROM payments
WHERE pay_method = 'CARD'
ORDER BY created_at DESC
LIMIT 20;

-- 常见索引失效或不走索引的情况（按常见程度和排查优先级排序）：
-- 1. 联合索引没有遵守 最左前缀 原则。
--    示例：索引是 (store_id, status, created_at)，却只写 WHERE status = 3
--    改法：补上最左列条件，或按查询场景创建 (status, created_at) 索引。
--    说明：如果查询场景是 store_id = ? AND status = ? AND created_at 范围查询，更适合创建 (store_id, status, created_at)；
--
--          WHERE 条件的书写顺序不影响索引使用，例如先写 created_at，再写 store_id、status，
--          优化器仍然可以按 (store_id, status, created_at) 的索引顺序使用索引。
--          真正要注意的是索引列顺序：如果索引建成 (created_at, store_id, status)，
--          会先按 created_at 做范围扫描，后面的 store_id、status 利用效果可能受限。
--

-- 2. 索引列，使用函数或表达式。
--    示例：WHERE DATE(created_at) = '2025-01-01'
--    改法：WHERE created_at >= '2025-01-01' AND created_at < '2025-01-02'
--

-- 3. LIKE 使用前置通配符 %。
--    示例：WHERE email LIKE '%demo.local'
--    改法：尽量使用前缀匹配，例如 WHERE email LIKE 'customer\_%'
--

-- 4. 隐式类型转换导致索引难以利用。
--    示例：phone 是 VARCHAR 类型，却写 WHERE phone = 13800138000
--    改法：WHERE phone = '13800138000'
--

-- 5. 范围查询后面的联合索引列，难以继续用于精确过滤或排序。
--    示例：索引是 (created_at, store_id, status)，WHERE created_at > '2025-01-01' AND store_id = 8
--    说明：created_at 是范围条件，后面的 store_id、status 利用效果可能受限。
--
可以，回答到第 5 个基本够用了，尤其是普通面试。
-- 6. SELECT * 导致大量回表，索引收益下降。
--    示例：WHERE total_amount > 4000 命中很多行，并且查询所有列。
--    改法：只查询必要字段，或设计覆盖索引。
--

-- 7. 查询命中行数太多，优化器认为全表扫描更便宜。
--    示例：WHERE status = 3 命中大量订单。
--    说明：低选择性字段即使建了索引，也不一定会被使用。
--

-- 8. ORDER BY / GROUP BY 字段顺序和索引顺序不匹配。
--    示例：索引是 (store_id, status, created_at)，但按 total_amount 排序。
--    说明：可能出现 Using filesort 或 Using temporary。
--

-- 9. OR 条件中有一侧没有合适索引。
--    示例：WHERE customer_id = 100 OR total_amount > 4000
--    说明：如果 OR 两边不能都高效走索引，优化器可能选择全表扫描。
--

-- 10. 使用不等于条件时，索引效果通常不稳定。
--     示例：WHERE status <> 0 或 WHERE status != 0
--     说明：如果返回数据比例很高，优化器可能选择全表扫描。
