export type CartItem = {
    id: string;
    name: string;
    price: number;
    quantity: number;
    imageUrl?: string;
};

export type CartContextType = {
    items: CartItem[];
    addToCart: (item: CartItem) => void;
    removeFromCart: (id: string) => void;
    updateQuantity: (itemId: string, quantity: number) => void;
    clearCart: () => void;
    isLoading: boolean;
}; 