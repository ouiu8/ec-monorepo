-- 商品テストデータ
INSERT INTO products (name, description, price, stock, image_url, created_at, updated_at) VALUES
('テスト商品1', 'テスト商品1の説明文です', 1000, 10, 'https://example.com/image1.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('テスト商品2', 'テスト商品2の説明文です', 2000, 20, 'https://example.com/image2.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('テスト商品3', 'テスト商品3の説明文です', 3000, 30, 'https://example.com/image3.jpg', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- カートのテストデータ
INSERT INTO carts (session_id, created_at, updated_at) VALUES
('test-session-1', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('test-session-2', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

-- カートアイテムのテストデータ
INSERT INTO cart_items (cart_id, product_id, quantity, created_at, updated_at) VALUES
(1, 1, 1, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(1, 2, 2, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
(2, 3, 3, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()); 