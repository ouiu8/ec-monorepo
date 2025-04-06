import { useCallback } from 'react';
import { CartItem } from '../types';

export const useCartOperations = (
    items: CartItem[],
    setItems: React.Dispatch<React.SetStateAction<CartItem[]>>
) => {
    const addToCart = useCallback((item: CartItem) => {
        setItems((prevItems) => {
            const existingItem = prevItems.find((i) => i.id === item.id);
            if (existingItem) {
                return prevItems.map((i) =>
                    i.id === item.id ? { ...i, quantity: i.quantity + item.quantity } : i
                );
            }
            return [...prevItems, item];
        });
    }, []);

    const removeFromCart = useCallback((id: string) => {
        setItems((prevItems) => {
            const newItems = prevItems.filter((item) => item.id !== id);
            if (newItems.length === 0) {
                localStorage.removeItem('ec-cart-items');
                localStorage.removeItem('ec-cart-timestamp');
            }
            return newItems;
        });
    }, []);

    const updateQuantity = useCallback((itemId: string, quantity: number) => {
        setItems((prevItems) =>
            prevItems.map((item) =>
                item.id === itemId ? { ...item, quantity } : item
            )
        );
    }, []);

    return { addToCart, removeFromCart, updateQuantity };
}; 