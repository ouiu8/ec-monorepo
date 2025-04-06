/*
============================================================
[ファイル概要]
ファイル名: CartProvider.tsx
目的: ショッピングカートの状態管理を行うコンテキストプロバイダー
技術スタック:
- TypeScript: 型安全性のため
- React (Context API): グローバルな状態管理のため
- Next.js: サーバーサイドレンダリングのため

参考ドキュメント:
- React Context: https://react.dev/reference/react/createContext
- TypeScript: https://www.typescriptlang.org/docs/handbook/react.html
- Next.js: https://nextjs.org/docs/app/building-your-application/data-fetching/client-side
============================================================
*/

/* 
[Next.js固有のディレクティブ]
'use client' → このコンポーネントをクライアントサイドでレンダリングすることを指示
理由: useState等のReactフックを使用するため
参考: https://nextjs.org/docs/getting-started/react-essentials#client-components
*/
'use client';

/* 
[必要な機能のインポート]
React関連の型とフック:
- createContext: コンテキストを作成するためのReact API
- useContext: 作成したコンテキストを使用するためのフック
- useState: 状態管理のためのフック
- ReactNode: React要素の型定義
*/
import { createContext, useContext, useState, ReactNode } from 'react';

/* 
[TypeScript] カートアイテムの型定義
type CartItem = {
  id: string;      → 商品を一意に識別するためのID（文字列型）
  name: string;     → 商品名（文字列型）
  price: number;    → 価格（数値型）
  quantity: number; → 数量（数値型）
};

理由: 型安全性を確保し、開発時のエラーを防ぐため
*/
type CartItem = {
  id: string;
  name: string;
  price: number;
  quantity: number;
};

/* 
[TypeScript] カートの機能を定義する型
type CartContextType = {
  // カート内の商品一覧を保持する配列
  items: CartItem[];          → CartItem型の配列（複数の商品を格納）
  
  // 商品をカートに追加する関数
  addItem: (item: CartItem) => void;  
  → 引数: CartItem型の商品情報
  → 戻り値: void（なし）
  
  // 商品をカートから削除する関数
  removeItem: (itemId: string) => void;
  → 引数: string型の商品ID
  → 戻り値: void（なし）
  
  // 商品の数量を更新する関数
  updateQuantity: (itemId: string, quantity: number) => void;
  → 引数1: string型の商品ID
  → 引数2: number型の数量
  → 戻り値: void（なし）
  
  // カートを空にする関数
  clearCart: () => void;
  → 引数: なし
  → 戻り値: void（なし）
};

理由：カートの機能をTypeScriptの型として定義することで、
1. 型安全性の確保
2. コード補完の有効化
3. 実装漏れの防止
を実現するため
*/
type CartContextType = {
  items: CartItem[];
  addItem: (item: CartItem) => void;
  removeItem: (itemId: string) => void;
  updateQuantity: (itemId: string, quantity: number) => void;
  clearCart: () => void;
};

/* 
[React] コンテキストの作成
createContext<CartContextType | null>(null)
- CartContextType | null → コンテキストの型（nullも許容）
- (null) → デフォルト値としてnullを設定

理由: アプリケーション全体で共有する状態を作成するため
*/
const CartContext = createContext<CartContextType | null>(null);

/* 
[React] カスタムフックの定義
useCart: カートの機能を簡単に使用するためのカスタムフック
- useContext(CartContext) → 作成したコンテキストを使用
- throw new Error → コンテキストが見つからない場合のエラー処理

理由: コンテキストの使用を簡略化し、型安全性を確保するため
*/
export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

/* 
[TypeScript] CartProviderの型定義
children: ReactNode → 子コンポーネントの型（React要素）

理由: プロバイダーコンポーネントが受け取るpropsの型を定義するため
*/
type CartProviderProps = {
  children: ReactNode;
};

/* 
[React] カートプロバイダーコンポーネント
export function CartProvider → 外部からインポート可能な関数コンポーネント
({ children }: CartProviderProps) → 型付きのprops

理由: カートの状態管理機能を提供するコンポーネントを作成するため
*/
export function CartProvider({ children }: CartProviderProps) {
  /* 
  [React] カートの状態管理
  useState<CartItem[]>([]) 
  - CartItem[] → 配列の型
  - [] → 初期値は空配列
  - items → 現在のカート内容
  - setItems → カート更新用の関数

  理由: カート内の商品リストを状態として管理するため
  */
  const [items, setItems] = useState<CartItem[]>([]);

  /* 
  [React] 商品追加機能
  addItem: 新しい商品をカートに追加または既存の商品の数量を更新
  - newItem: CartItem → 追加する商品情報
  - currentItems => ... → 現在の状態を基に新しい状態を計算
  - find → 既存の商品を検索
  - map → 配列の各要素を変換
  - spread演算子(...) → オブジェクトのコピーを作成

  理由: カートへの商品追加処理を一元管理するため
  */
  const addItem = (newItem: CartItem) => {
    setItems(currentItems => {
      const existingItem = currentItems.find(item => item.id === newItem.id);
      if (existingItem) {
        return currentItems.map(item =>
          item.id === newItem.id
            ? { ...item, quantity: item.quantity + newItem.quantity }
            : item
        );
      }
      return [...currentItems, newItem];
    });
  };

  /* 
  [React] 商品削除機能
  removeItem: 指定されたIDの商品をカートから削除
  - itemId: string → 削除する商品のID
  - filter → 条件に合う要素のみを残す

  理由: カートからの商品削除機能を提供するため
  */
  const removeItem = (itemId: string) => {
    setItems(currentItems => currentItems.filter(item => item.id !== itemId));
  };

  /* 
  [React] 数量更新機能
  updateQuantity: 指定された商品の数量を更新
  - itemId: string → 更新する商品のID
  - quantity: number → 新しい数量
  - map → 配列の各要素を変換

  理由: カート内の商品数量を更新する機能を提供するため
  */
  const updateQuantity = (itemId: string, quantity: number) => {
    setItems(currentItems =>
      currentItems.map(item =>
        item.id === itemId ? { ...item, quantity } : item
      )
    );
  };

  /* 
  [React] カートクリア機能
  clearCart: カートを空にする
  setItems([]) → 空配列で状態を更新

  理由: カート内の全商品を一括で削除する機能を提供するため
  */
  const clearCart = () => {
    setItems([]);
  };

  /* 
  [React] コンテキストプロバイダーのレンダリング
  CartContext.Provider → コンテキストの提供
  value → コンテキストで提供する値（オブジェクト）
  children → 子コンポーネント

  理由: カートの機能をアプリケーション全体で利用可能にするため
  */
  return (
    <CartContext.Provider
      value={{
        items,
        addItem,
        removeItem,
        updateQuantity,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
} 