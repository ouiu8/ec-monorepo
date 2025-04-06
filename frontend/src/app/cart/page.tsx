/* 
[概要]
このファイル (page.tsx) は、ECサイトのカートページです。
CartContext からカートの状態（追加された商品一覧）を取得し、ユーザーに表示します。
各商品は、削除ボタンなどで個別に管理できるように実装します。

[技術スタック]
- Next.js 13 (App Router): ページコンポーネントとして機能
- TypeScript: 型安全なコードを記述
- Tailwind CSS: スタイリングのためのユーティリティクラスを使用
- React Context API: グローバルなカート状態を管理
*/

"use client"; // このページはクライアントコンポーネントとして扱います

import { useCart } from '../../context/cart';
// 上記は、以前作成した CartContext からカート状態と操作関数を取得するためのフック

export default function CartPage() {
  // useCart フックを使って、カートの中身（items）と削除関数（removeFromCart）を取得
  const { items, removeFromCart, isLoading } = useCart();
  
  console.log('CartPage: 現在のカートの中身:', items);

  // ローディング中の表示
  if (isLoading) {
    return (
      <div className="p-4 text-center">
        <h1 className="text-2xl font-bold text-gray-700">読み込み中...</h1>
      </div>
    );
  }

  // カートが空の場合のメッセージ表示
  if (items.length === 0) {
    return (
      <div className="p-4 text-center">
        <h1 className="text-2xl font-bold text-gray-700">カートは空です</h1>
        <p className="mt-2 text-gray-500">商品を追加してください</p>
      </div>
    );
  }

  return (
    /* 
      全体のコンテナ
      "p-4": 全方向に1remのパディング
      "max-w-4xl mx-auto": 最大幅を設定し、中央揃え
    */
    <div className="p-4 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold mb-4">カートの中身</h1>
      {/*
        カート内の各アイテムをリスト表示。
        items.map(...) を使って、各商品に対してカード形式で表示します。
      */}
      {items.map((item) => (
        <div key={item.id} className="border rounded shadow p-4 bg-white mb-4 flex justify-between items-center">
          {/* 商品情報の表示 */}
          <div className="flex items-center space-x-4">
            {item.imageUrl && (
              <img 
                src={item.imageUrl} 
                alt={item.name} 
                className="w-20 h-20 object-contain"
              />
            )}
            <div>
              <h2 className="text-xl font-bold">{item.name}</h2>
              <p className="text-gray-700">数量: {item.quantity}</p>
              <p className="text-gray-700">価格: {item.price}円</p>
            </div>
          </div>
          {/*
            削除ボタン
            "px-4 py-2": パディング設定
            "bg-red-500": 赤背景
            "text-white": 文字色を白に
            "rounded": 角を丸める
            "hover:bg-red-600": ホバー時に背景色を濃くする
            "transition": アニメーションを滑らかに
          */}
          <button
            onClick={() => removeFromCart(item.id)}
            className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600 transition"
          >
            削除
          </button>
        </div>
      ))}
      {/*
        カート内の商品の合計金額の計算（仮の実装例）
        ここでは単純に各アイテムの数量と仮の価格を掛けた値の合計を表示します。
        ※ 実際の価格情報は、商品データと連携させる必要があります。
      */}
      <div className="mt-4 text-right">
        <span className="text-xl font-bold">
          合計金額: {items.reduce((total, item) => total + item.price * item.quantity, 0)}円
        </span>
      </div>
    </div>
  );
}