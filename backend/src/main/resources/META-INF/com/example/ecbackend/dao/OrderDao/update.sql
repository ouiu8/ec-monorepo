UPDATE orders
SET user_id = /* order.userId */1,
    order_date = /* order.orderDate */CURRENT_TIMESTAMP,
    status = /* order.status */'PENDING',
    total_amount = /* order.totalAmount */0,
    shipping_address = /* order.shippingAddress */'',
    payment_method = /* order.paymentMethod */'',
    cancelled_at = /* order.cancelledAt */null,
    updated_at = CURRENT_TIMESTAMP
WHERE id = /* order.id */1