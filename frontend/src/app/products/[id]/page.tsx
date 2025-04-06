/* 
[概要]
このファイル (page.tsx) は、動的ルーティングを利用した商品詳細ページです。
URL から商品IDを取得し、モックデータを使って該当商品の詳細情報を表示します。

[技術スタック]
- Next.js 13 (App Router): 動的ルーティング機能を利用
- TypeScript: 型安全なコードを記述
- Tailwind CSS: ユーティリティクラスを使用してスタイリング
*/

'use client';

/* 
[インポート]
Next.js の useParams フックを使って、URL の動的パラメータ (ここでは "id") を取得します。
*/
import { useParams } from 'next/navigation';
import { useCart } from '../../../context/cart'; // カートコンテキストをインポート


/* 
[モックデータ]
仮の商品の情報を定義します。通常はバックエンド API からデータを取得しますが、
ここではデモ用に固定の配列を使用します。
*/
const mockProducts = [
    {
        id: '1',            /* 商品ID (文字列) */
        name: '商品A',       /* 商品名 */
        description: 'これは商品Aの詳細な説明です。',  /* 商品の説明 */
        price: 1000,        /* 価格 */
        imageUrl: '/images/product-a.jpg', /* 商品画像のパス（public/images/ 内に配置） */
    },
    {
        id: '2',
        name: '商品B',
        description: 'これは商品Bの詳細な説明です。',
        price: 2000,
        imageUrl: '/images/product-b.jpg',
    },
    {
        id: '3',
        name: '商品C',
        description: 'これは商品Cの詳細な説明です。',
        price: 3000,
        imageUrl: '/images/product-c.jpg',
    },
];

/* 
[コンポーネント定義]
このコンポーネントは URL から取得した商品IDを元に、該当する商品の詳細情報を表示します。
*/
export default function ProductDetail() {
    // useParams を利用して URL パラメータを取得
    const params = useParams();
    const { id } = params as { id: string };

    // モックデータから、指定されたIDの商品を検索
    const product = mockProducts.find((p) => p.id === id);

    // カートコンテキストから addToCart 関数を取得
    const { addToCart } = useCart();

    // 商品が見つからなかった場合のエラーハンドリング
    if (!product) {
        return (
            <div className="p-4">
                <h1 className="text-2xl font-bold text-red-500">
                    商品が見つかりません
                </h1>
            </div>
        );
    }

    // カートに追加するボタンがクリックされたときのハンドラ
    const handleAddToCart = () => {
        console.log('商品をカートに追加:', { 
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: 1,
            imageUrl: product.imageUrl
        });
        // 商品をカートに追加 (商品の全情報を含める)
        addToCart({ 
            id: product.id,
            name: product.name,
            price: product.price,
            quantity: 1,
            imageUrl: product.imageUrl
        });
    };

    return (
        /* 
          [全体のコンテナ]
          p-4: 全方向に1remのパディング
          max-w-4xl: 最大幅を設定して中央寄せ
          mx-auto: 水平方向のマージンを自動にして中央に配置
        */
        <div className="p-4 max-w-4xl mx-auto">
            {/*
        [商品画像の表示]
        w-full: 横幅100%に
        h-80: 高さを固定 (例: 20rem)
        object-contain: 画像のアスペクト比を維持して全体表示
        mb-4: 下方向の余白
      */}
            <img
                src={product.imageUrl}
                alt={product.name}
                className="w-full h-80 object-contain mb-4"
            />

            {/*
        [商品名の表示]
        text-3xl: 文字サイズを3XLに
        font-bold: 太字
        mb-2: 下方向の余白
      */}
            <h1 className="text-3xl font-bold mb-2">{product.name}</h1>

            {/*
        [商品価格の表示]
        text-xl: 文字サイズをXLに
        text-gray-700: 文字色をグレーに
        mb-4: 下方向の余白
      */}
            <p className="text-xl text-gray-700 mb-4">{product.price}円</p>

            {/*
        [商品説明の表示]
        text-base: 基本の文字サイズ
        text-gray-600: やや薄いグレーの文字色
      */}
            <p className="text-base text-gray-600">{product.description}</p>
            {/* カートに追加するボタン */}
            <button
                onClick={handleAddToCart}
                className="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600 transition"
            >
                カートに追加
            </button>
        </div>
    );
}