-- テスト用サンプルデータ

-- シーケンスリセット
ALTER SEQUENCE products_id_seq RESTART WITH 1;
ALTER SEQUENCE carts_id_seq RESTART WITH 1;
ALTER SEQUENCE cart_items_id_seq RESTART WITH 1;
ALTER SEQUENCE orders_id_seq RESTART WITH 1;
ALTER SEQUENCE order_items_id_seq RESTART WITH 1;

-- サンプル商品データ挿入（正確に3件のみ）
INSERT INTO products (name, description, price, stock, image_url, created_at, updated_at) VALUES 
('テスト商品1', 'テスト商品1の説明です', 1000, 10, 'https://example.com/image1.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('テスト商品2', 'テスト商品2の説明です', 2000, 20, 'https://example.com/image2.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('テスト商品3', 'テスト商品3の説明です', 3000, 30, 'https://example.com/image3.jpg', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- テスト用のカート
INSERT INTO carts (session_id, created_at, updated_at) VALUES 
('test-session-1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('test-session-2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- テスト用のカートアイテム
INSERT INTO cart_items (cart_id, product_id, quantity, created_at, updated_at) VALUES 
(1, 1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- テスト用の注文
INSERT INTO orders (user_id, order_date, status, total_amount, shipping_address, payment_method, created_at, updated_at) VALUES 
('test-user-1', CURRENT_TIMESTAMP, 'PENDING', 5000, 'テスト住所1', 'CREDIT_CARD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('test-user-2', CURRENT_TIMESTAMP, 'SHIPPED', 7000, 'テスト住所2', 'BANK_TRANSFER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- テスト用の注文明細
INSERT INTO order_items (order_id, product_id, quantity, price, created_at, updated_at) VALUES 
(1, 1, 2, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 3, 1, 3000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 3, 2000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 1, 1000, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 