"use client";

import { CartProvider } from '../context/cart';
import { ReactNode } from 'react';

type ClientCartProviderProps = {
  children: ReactNode;
};

export default function ClientCartProvider({ children }: ClientCartProviderProps) {
  return <CartProvider>{children}</CartProvider>;
} 