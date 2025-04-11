UPDATE cart_items
SET quantity = /* cartItem.quantity */1,
    updated_at = CURRENT_TIMESTAMP
WHERE id = /* cartItem.id */1 