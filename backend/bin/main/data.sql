INSERT INTO products (name, description, price) VALUES
('商品1', '商品1の説明文です', 1000),
('商品2', '商品2の説明文です', 2000),
('商品3', '商品3の説明文です', 3000)
ON CONFLICT (id) DO NOTHING; 