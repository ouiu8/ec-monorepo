SELECT id, cart_id, product_id, quantity, created_at, updated_at
FROM cart_items
WHERE cart_id = /* cartId */1 