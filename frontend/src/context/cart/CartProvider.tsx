import { ReactNode } from 'react';
import { CartContext } from './CartContext';
import { useCartStorage } from './hooks/useCartStorage';
import { useCartOperations } from './hooks/useCartOperations';

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const { items, setItems, isLoading } = useCartStorage();
    const { addToCart, removeFromCart, updateQuantity } = useCartOperations(items, setItems);

    const clearCart = () => {
        setItems([]);
        localStorage.removeItem('ec-cart-items');
        localStorage.removeItem('ec-cart-timestamp');
    };

    return (
        <CartContext.Provider value={{
            items,
            addToCart,
            removeFromCart,
            updateQuantity,
            clearCart,
            isLoading
        }}>
            {children}
        </CartContext.Provider>
    );
}; 