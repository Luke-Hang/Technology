USE master;
GO

IF DB_ID(N'explain_lab_sqlserver') IS NOT NULL
BEGIN
  ALTER DATABASE explain_lab_sqlserver SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
  DROP DATABASE explain_lab_sqlserver;
END;
GO

CREATE DATABASE explain_lab_sqlserver;
GO

USE explain_lab_sqlserver;
GO

SET NOCOUNT ON;
SET STATISTICS IO, TIME ON;
GO

IF OBJECT_ID(N'dbo.Payments', N'U') IS NOT NULL DROP TABLE dbo.Payments;
IF OBJECT_ID(N'dbo.OrderItems', N'U') IS NOT NULL DROP TABLE dbo.OrderItems;
IF OBJECT_ID(N'dbo.Orders', N'U') IS NOT NULL DROP TABLE dbo.Orders;
IF OBJECT_ID(N'dbo.Products', N'U') IS NOT NULL DROP TABLE dbo.Products;
IF OBJECT_ID(N'dbo.Customers', N'U') IS NOT NULL DROP TABLE dbo.Customers;
GO

CREATE TABLE dbo.Customers (
  id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Customers PRIMARY KEY,
  name NVARCHAR(60) NOT NULL,
  email NVARCHAR(120) NOT NULL,
  city NVARCHAR(40) NOT NULL,
  status TINYINT NOT NULL,
  created_at DATETIME2(0) NOT NULL
);

CREATE TABLE dbo.Products (
  id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Products PRIMARY KEY,
  sku NVARCHAR(40) NOT NULL,
  category_id INT NOT NULL,
  brand NVARCHAR(40) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  stock INT NOT NULL,
  status TINYINT NOT NULL,
  created_at DATETIME2(0) NOT NULL
);

CREATE TABLE dbo.Orders (
  id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Orders PRIMARY KEY,
  order_no NVARCHAR(40) NOT NULL,
  customer_id BIGINT NOT NULL,
  store_id INT NOT NULL,
  status TINYINT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  created_at DATETIME2(0) NOT NULL,
  paid_at DATETIME2(0) NULL
);

CREATE TABLE dbo.OrderItems (
  id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_OrderItems PRIMARY KEY,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(10,2) NOT NULL,
  created_at DATETIME2(0) NOT NULL
);

CREATE TABLE dbo.Payments (
  id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_Payments PRIMARY KEY,
  order_id BIGINT NOT NULL,
  pay_method NVARCHAR(20) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  status TINYINT NOT NULL,
  created_at DATETIME2(0) NOT NULL
);
GO

-- 造数：Customers 20,000。
WITH nums AS (
  SELECT TOP (20000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS i
  FROM sys.all_objects a CROSS JOIN sys.all_objects b
)
INSERT INTO dbo.Customers(name, email, city, status, created_at)
SELECT
  CONCAT(N'customer_', i),
  CONCAT(N'customer_', i, N'@demo.local'),
  CHOOSE(1 + i % 10, N'London', N'Manchester', N'Birmingham', N'Leeds', N'Glasgow', N'Liverpool', N'Bristol', N'Oxford', N'Cambridge', N'Cardiff'),
  IIF(i % 20 = 0, 0, 1),
  DATEADD(DAY, i % 900, CONVERT(DATETIME2(0), '2023-01-01'))
FROM nums;

-- 造数：Products 5,000。
WITH nums AS (
  SELECT TOP (5000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS i
  FROM sys.all_objects a CROSS JOIN sys.all_objects b
)
INSERT INTO dbo.Products(sku, category_id, brand, price, stock, status, created_at)
SELECT
  CONCAT(N'SKU-', RIGHT(CONCAT(N'000000', i), 6)),
  1 + i % 80,
  CHOOSE(1 + i % 12, N'Sony', N'Apple', N'Dell', N'HP', N'Lenovo', N'Samsung', N'Xiaomi', N'Canon', N'Nike', N'Adidas', N'Bosch', N'LG'),
  CAST(10 + (i * 37) % 3000 + (i % 99) / 100.0 AS DECIMAL(10,2)),
  (i * 13) % 500,
  IIF(i % 25 = 0, 0, 1),
  DATEADD(DAY, i % 800, CONVERT(DATETIME2(0), '2023-01-01'))
FROM nums;

-- 造数：Orders 100,000。
WITH nums AS (
  SELECT TOP (100000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS i
  FROM sys.all_objects a CROSS JOIN sys.all_objects b
)
INSERT INTO dbo.Orders(order_no, customer_id, store_id, status, total_amount, created_at, paid_at)
SELECT
  CONCAT(N'ORD-', RIGHT(CONCAT(N'00000000', i), 8)),
  1 + (i * 17) % 20000,
  1 + i % 60,
  CASE i % 10 WHEN 0 THEN 0 WHEN 1 THEN 1 WHEN 2 THEN 2 ELSE 3 END,
  CAST(20 + ((i * 97) % 50000) / 10.0 AS DECIMAL(12,2)),
  DATEADD(SECOND, i % 86400, DATEADD(DAY, i % 700, CONVERT(DATETIME2(0), '2024-01-01'))),
  CASE WHEN i % 10 IN (0, 1) THEN NULL
       ELSE DATEADD(SECOND, (i + 300) % 86400, DATEADD(DAY, i % 700, CONVERT(DATETIME2(0), '2024-01-01')))
  END
FROM nums;

-- 造数：OrderItems 约 300,000。
INSERT INTO dbo.OrderItems(order_id, product_id, quantity, unit_price, created_at)
SELECT
  o.id,
  1 + (o.id * v.j * 19) % 5000,
  1 + (o.id + v.j) % 4,
  CAST(10 + (o.id * v.j * 23) % 3000 / 10.0 AS DECIMAL(10,2)),
  DATEADD(DAY, o.id % 700, CONVERT(DATETIME2(0), '2024-01-01'))
FROM dbo.Orders o
CROSS APPLY (VALUES (1), (2), (3), (4), (5)) v(j)
WHERE v.j <= 1 + o.id % 5;

-- 造数：Payments 95,000。
WITH nums AS (
  SELECT TOP (95000) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS i
  FROM sys.all_objects a CROSS JOIN sys.all_objects b
)
INSERT INTO dbo.Payments(order_id, pay_method, amount, status, created_at)
SELECT
  i,
  CHOOSE(1 + i % 5, N'CARD', N'CASH', N'PAYPAL', N'APPLE_PAY', N'BANK'),
  CAST(20 + ((i * 97) % 50000) / 10.0 AS DECIMAL(12,2)),
  IIF(i % 13 = 0, 0, 1),
  DATEADD(SECOND, (i + 600) % 86400, DATEADD(DAY, i % 700, CONVERT(DATETIME2(0), '2024-01-01')))
FROM nums;
GO

UPDATE STATISTICS dbo.Customers WITH FULLSCAN;
UPDATE STATISTICS dbo.Products WITH FULLSCAN;
UPDATE STATISTICS dbo.Orders WITH FULLSCAN;
UPDATE STATISTICS dbo.OrderItems WITH FULLSCAN;
UPDATE STATISTICS dbo.Payments WITH FULLSCAN;
GO

-- 使用说明：
-- 1. 在 SSMS 中建议打开“包括实际执行计划”（Ctrl + M），再执行下面的查询。
-- 2. Azure Data Studio 可以使用 Explain / Actual Plan 查看执行计划。
-- 3. 重点看执行计划里的 Index Seek、Index Scan、Table Scan、Key Lookup、Sort、Hash Match，以及消息窗口里的 logical reads。

-- 基础检查：确认造数是否成功。
SELECT COUNT(*) AS customers_count FROM dbo.Customers;
SELECT COUNT(*) AS products_count FROM dbo.Products;
SELECT COUNT(*) AS orders_count FROM dbo.Orders;
SELECT COUNT(*) AS order_items_count FROM dbo.OrderItems;
SELECT COUNT(*) AS payments_count FROM dbo.Payments;
GO

-- 练习 0: 主键和普通索引查询。
-- 作用：观察主键等值查询通常是 Clustered Index Seek；普通索引等值查询通常是 Index Seek。
SELECT * FROM dbo.Customers WHERE id = 1;

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_order_no ON dbo.Orders(order_no);
-- DROP INDEX idx_orders_order_no ON dbo.Orders;

SELECT * FROM dbo.Orders WHERE order_no = N'ORD-00000001';
GO

-- 练习 1: 普通范围查询，索引不一定会被使用。
-- 说明：total_amount > 4000 是范围查询，命中行数较多，并且 SELECT * 可能触发 Key Lookup，优化器可能选择扫描。
SELECT * FROM dbo.Orders WHERE total_amount > 4000;

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_total_amount ON dbo.Orders(total_amount);
-- DROP INDEX idx_orders_total_amount ON dbo.Orders;

SELECT * FROM dbo.Orders WHERE total_amount > 4000;

-- 条件范围更小时，索引更可能真正生效。
SELECT * FROM dbo.Orders WHERE total_amount BETWEEN 4900 AND 4910;

-- 只查索引字段时，更容易形成覆盖索引，减少 Key Lookup。
SELECT total_amount FROM dbo.Orders WHERE total_amount > 4000;
GO

-- 练习 2: 联合索引的最左前缀。
-- 作用：理解 (store_id, status, created_at) 为什么适合 store_id + status + created_at 排序/范围场景。
EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_store_status_created ON dbo.Orders(store_id, status, created_at);
-- DROP INDEX idx_orders_store_status_created ON dbo.Orders;

SELECT * FROM dbo.Orders
WHERE store_id = 8 AND status = 3
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;
GO

-- 练习 3: 不满足联合索引最左前缀。
-- 作用：只查 status 时，无法高效利用 (store_id, status, created_at)，因为跳过了最左列 store_id。
SELECT * FROM dbo.Orders
WHERE status = 3
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_status_created ON dbo.Orders(status, created_at);
-- DROP INDEX idx_orders_status_created ON dbo.Orders;

SELECT * FROM dbo.Orders
WHERE status = 3
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;
GO

-- 练习 4: 函数或表达式导致索引难以利用。
-- 作用：不要在索引列上包函数；CONVERT(date, created_at) 会让范围定位变差。
SELECT * FROM dbo.Orders
WHERE created_at >= '2025-01-01'
  AND created_at < '2025-01-02';

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_created ON dbo.Orders(created_at);
-- DROP INDEX idx_orders_created ON dbo.Orders;

SELECT * FROM dbo.Orders
WHERE CONVERT(date, created_at) = '2025-01-01';

SELECT * FROM dbo.Orders
WHERE created_at >= '2025-01-01'
  AND created_at < '2025-01-02';
GO

-- 练习 5: 覆盖索引和 Key Lookup。
-- 作用：查询字段都在非聚集索引里时，可以减少回表；SQL Server 执行计划里常见 Key Lookup。
SELECT customer_id, created_at FROM dbo.Orders
WHERE customer_id = 123
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_customer_created ON dbo.Orders(customer_id, created_at);
-- DROP INDEX idx_orders_customer_created ON dbo.Orders;

SELECT customer_id, created_at FROM dbo.Orders
WHERE customer_id = 123
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;

-- INCLUDE 可以让索引覆盖更多返回列，减少 Key Lookup。
EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_customer_created_include ON dbo.Orders(customer_id, created_at) INCLUDE (order_no, total_amount, status);
-- DROP INDEX idx_orders_customer_created_include ON dbo.Orders;

SELECT order_no, customer_id, created_at, total_amount, status FROM dbo.Orders
WHERE customer_id = 123
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
GO

-- 练习 6: JOIN 的连接字段索引。
-- 作用：观察多表 JOIN 时，每张表使用的访问方式，以及 logical reads 是否下降。
EXEC sp_helpindex N'dbo.Payments';
CREATE INDEX idx_payments_order ON dbo.Payments(order_id);
-- DROP INDEX idx_payments_order ON dbo.Payments;

SELECT o.id, o.order_no, c.name, p.pay_method
FROM dbo.Orders o
JOIN dbo.Customers c ON c.id = o.customer_id
LEFT JOIN dbo.Payments p ON p.order_id = o.id
WHERE o.store_id = 8
  AND o.status = 3
  AND o.created_at >= '2025-01-01'
  AND o.created_at < '2025-02-01'
ORDER BY o.created_at DESC
OFFSET 0 ROWS FETCH NEXT 50 ROWS ONLY;
GO

-- 练习 7: LIKE 前缀匹配和前置通配符。
-- 作用：'abc%' 更容易使用索引范围扫描，'%abc' 通常无法高效利用普通 B-Tree 索引。
SELECT * FROM dbo.Customers WHERE email LIKE N'customer[_]12%';
SELECT * FROM dbo.Customers WHERE email LIKE N'%12@demo.local';

EXEC sp_helpindex N'dbo.Customers';
CREATE INDEX idx_customers_email ON dbo.Customers(email);
-- DROP INDEX idx_customers_email ON dbo.Customers;

SELECT * FROM dbo.Customers WHERE email LIKE N'customer[_]12%';
SELECT * FROM dbo.Customers WHERE email LIKE N'%12@demo.local';
GO

-- 练习 8: GROUP BY 优化。
-- 作用：观察 GROUP BY 在没有合适索引时，可能出现 Sort 或 Hash Match Aggregate。
SELECT store_id, status, COUNT(*) AS order_count, SUM(total_amount) AS amount_sum
FROM dbo.Orders
WHERE created_at >= '2025-01-01'
GROUP BY store_id, status;

EXEC sp_helpindex N'dbo.Orders';
CREATE INDEX idx_orders_created_store_status ON dbo.Orders(created_at, store_id, status);
-- DROP INDEX idx_orders_created_store_status ON dbo.Orders;

SELECT store_id, status, COUNT(*) AS order_count, SUM(total_amount) AS amount_sum
FROM dbo.Orders
WHERE created_at >= '2025-01-01'
GROUP BY store_id, status;
GO

-- 练习 9: ORDER BY 与 Sort。
-- 作用：观察没有合适索引时，WHERE + ORDER BY 可能出现 Sort。
SELECT * FROM dbo.Payments
WHERE pay_method = N'CARD'
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;

EXEC sp_helpindex N'dbo.Payments';
CREATE INDEX idx_payments_method_created ON dbo.Payments(pay_method, created_at);
-- DROP INDEX idx_payments_method_created ON dbo.Payments;

SELECT * FROM dbo.Payments
WHERE pay_method = N'CARD'
ORDER BY created_at DESC
OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY;
GO

-- SQL Server 常见索引失效或不走索引的情况（按常见程度和排查优先级排序）：
-- 1. 联合索引没有遵守最左前缀原则。
--    示例：索引是 (store_id, status, created_at)，却只写 WHERE status = 3
--    说明：WHERE 条件书写顺序不影响索引使用，真正重要的是 CREATE INDEX 时的列顺序。
--
-- 2. 在索引列上使用函数或表达式。
--    示例：WHERE CONVERT(date, created_at) = '2025-01-01'
--    改法：WHERE created_at >= '2025-01-01' AND created_at < '2025-01-02'
--
-- 3. LIKE 使用前置通配符。
--    示例：WHERE email LIKE N'%demo.local'
--    改法：尽量使用前缀匹配，例如 WHERE email LIKE N'customer[_]%'
--
-- 4. 隐式类型转换导致索引难以利用。
--    示例：phone 是 NVARCHAR 类型，却写 WHERE phone = 13800138000
--    改法：WHERE phone = N'13800138000'
--
-- 5. 范围查询后面的联合索引列难以继续用于精确过滤或排序。
--    示例：索引是 (created_at, store_id, status)，WHERE created_at > '2025-01-01' AND store_id = 8
--
-- 6. SELECT * 导致大量 Key Lookup，索引收益下降。
--    改法：只查询必要字段，或使用 INCLUDE 设计覆盖索引。
--
-- 7. 查询命中行数太多，优化器认为扫描更便宜。
--    示例：WHERE status = 3 命中大量订单。
--
-- 8. ORDER BY / GROUP BY 字段顺序和索引顺序不匹配。
--    说明：可能出现 Sort、Hash Match Aggregate 或较高 logical reads。
--
-- 9. OR 条件中有一侧没有合适索引。
--    示例：WHERE customer_id = 100 OR total_amount > 4000
--
-- 10. 使用不等于条件时，索引效果通常不稳定。
--     示例：WHERE status <> 0 或 WHERE status != 0
