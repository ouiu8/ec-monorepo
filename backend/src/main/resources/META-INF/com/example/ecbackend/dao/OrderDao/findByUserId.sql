SELECT id, user_id, order_date, status, total_amount, shipping_address, payment_method, cancelled_at, created_at, updated_at
FROM orders
WHERE user_id = /* userId */1
ORDER BY order_date DESC 