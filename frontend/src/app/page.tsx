/* 
[概要]
このファイル (page.tsx) は ECサイトのトップページを定義。
仮データ (mockProducts) を使って商品一覧を表示し、Tailwind CSS でカード風のレイアウトを作る。

[技術スタック]
- 【React】(JSX): UIコンポーネントを作成
- 【TypeScript】: 型安全性を向上
- 【Tailwind CSS】: ユーティリティクラスを使ってデザイン
*/

/* 
[モックデータ - 仮の商品情報]
本来はAPIやデータベースから取得するところを、まずは手動で配列を定義してテスト。
*/
const mockProducts = [
  {
    id: 1, // 商品ID（数値）
    name: '商品A', // 商品名（文字列）
    price: 1000, // 価格（数値）
    imageUrl: '/images/product-a.jpg', // 商品画像のパス（public/images/内）
  },
  {
    id: 2,
    name: '商品B',
    price: 2000,
    imageUrl: '/images/product-b.jpg',
  },
  {
    id: 3,
    name: '商品C',
    price: 3000,
    imageUrl: '/images/product-c.jpg',
  },
];

/* 
[コンポーネント定義]
Next.js 13 の App Router では、page.tsx のデフォルトエクスポートが "/" (ルート) に対応。
*/
export default function Home() {
  return (
    /* 
      [グリッドレイアウト用のラッパーdiv]
      p-4           : 全方向に1rem(16px)の余白
      grid          : display: grid を適用
      grid-cols-1   : 1列のグリッド (狭い画面用)
      md:grid-cols-3: 768px以上の画面幅では3列に
      gap-4         : グリッドアイテム間の隙間を1remに
    */
    <div className="p-4 grid grid-cols-1 md:grid-cols-3 gap-4">
      {/*
        [商品カードのループ表示]
        mockProducts.map(...) : 配列内の各商品をカードにして表示
        key={product.id}      : React が要素を一意に識別するために必須
      */}
      {mockProducts.map((product) => (
        /* 
          [カードコンテナ]
          border : 枠線を表示
          rounded: 角を丸くする
          shadow : 影を付ける
          p-4    : パディング(16px)
          bg-white : 背景を白に
        */
        <div key={product.id} className="border rounded shadow p-4 bg-white">
          {/*
            [商品画像]
            w-full       : 幅を100%に
            h-40         : 高さを10rem(160px)に固定
            object-cover : 画像の縦横比を維持しつつ要素を埋める
            mb-2         : 下方向に8pxの余白
          */}
          <img
            src={product.imageUrl}
            alt={product.name}
            className="w-full h-auto object-auto mb-2"
          />

          {/*
            [商品名]
            text-xl   : 文字サイズをXL (約1.25rem)
            font-bold : 太字
            mb-1      : 下に4pxの余白
          */}
          <h2 className="text-xl font-bold mb-1">{product.name}</h2>

          {/*
            [商品価格]
            text-gray-700 : 文字色を少し薄いグレーに
            {product.price}円 : 価格を表示し、日本円の単位を付ける
          */}
          <p className="text-gray-700">{product.price}円</p>
        </div>
      ))}
    </div>
  );
}