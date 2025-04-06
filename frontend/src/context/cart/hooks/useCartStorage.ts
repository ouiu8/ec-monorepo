import { useState, useEffect } from 'react';
import { CartItem } from '../types';

const CART_STORAGE_KEY = 'ec-cart-items';
const CART_TIMESTAMP_KEY = 'ec-cart-timestamp';
const CART_EXPIRY_HOURS = 24;

export const useCartStorage = () => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const loadCartData = () => {
            const savedItems = localStorage.getItem(CART_STORAGE_KEY);
            const timestamp = localStorage.getItem(CART_TIMESTAMP_KEY);
            
            if (timestamp) {
                const savedTime = new Date(timestamp).getTime();
                const currentTime = new Date().getTime();
                const hoursPassed = (currentTime - savedTime) / (1000 * 60 * 60);
                
                if (hoursPassed > CART_EXPIRY_HOURS) {
                    localStorage.removeItem(CART_STORAGE_KEY);
                    localStorage.removeItem(CART_TIMESTAMP_KEY);
                    setItems([]);
                    setIsLoading(false);
                    return;
                }
            }
            
            if (savedItems) {
                setItems(JSON.parse(savedItems));
            }
            setIsLoading(false);
        };

        loadCartData();
    }, []);

    useEffect(() => {
        if (!isLoading && items.length > 0) {
            localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items));
            localStorage.setItem(CART_TIMESTAMP_KEY, new Date().toISOString());
        }
    }, [items, isLoading]);

    return { items, setItems, isLoading };
}; 