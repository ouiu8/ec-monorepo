UPDATE order_items
SET order_id = /* orderItem.orderId */1,
    product_id = /* orderItem.productId */1,
    quantity = /* orderItem.quantity */1,
    unit_price = /* orderItem.unitPrice */0,
    subtotal = /* orderItem.subtotal */0,
    updated_at = CURRENT_TIMESTAMP
WHERE id = /* orderItem.id */1 