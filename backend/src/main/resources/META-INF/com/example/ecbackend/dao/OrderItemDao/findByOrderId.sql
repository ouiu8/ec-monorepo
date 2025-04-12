SELECT id, order_id, product_id, quantity, unit_price, subtotal, created_at, updated_at
FROM order_items
WHERE order_id = /* orderId */1
ORDER BY id ASC 