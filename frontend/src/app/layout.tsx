/* 
[概要]
このファイル (layout.tsx) は Next.js 13 (App Router) のルートレイアウトコンポーネント。
すべてのページに共通するヘッダー、フッター、<html>構造などを定義し、children で各ページのコンテンツを表示する。
*/

import './globals.css';  // グローバルCSSをインポート
import ClientCartProvider from '../components/ClientCartProvider'; // クライアントコンポーネントとして分離したCartProvider

/* 
[技術スタック]
- 【React】(JSX): UIコンポーネントを作成
- 【TypeScript】: 型安全性を向上
- 【Tailwind CSS】: ユーティリティクラスを使ってスタイリング
*/

/* 
[レイアウト全体の構造]
<html>
  <head>  ...  </head>
  <body className="...">
    <header> ... </header>
    <main> {children} </main>
    <footer> ... </footer>
  </body>
</html>
*/

/* 【型定義】React.ReactNode - 子要素 (各ページのコンテンツ) を表す */
type RootLayoutProps = {
  children: React.ReactNode;
};

/* 
【エクスポート】ルートレイアウトコンポーネント
Next.js 13 の App Router では、src/app/layout.tsx に定義したデフォルトエクスポートが
全ページに共通するレイアウトとして使われる。
*/
export default function RootLayout({ children }: RootLayoutProps) {
  return (
    // <html> タグ: ドキュメントのルート要素
    <html lang="ja">
      <head>
        {/* <title>タグは App Router の場合、ここで設定してもOK。metaタグなども同様 */}
        <title>ECサイト</title>
      </head>
      {/* <body> タグ: Tailwind CSS のクラスで背景色や文字色を設定 */}
      <body className="bg-gray-100 text-gray-900">
        <ClientCartProvider>
          {/*
            [ヘッダー] 
            p-4         : パディング(16px)を適用
            bg-blue-500 : 背景を青 (blue-500)
            text-white  : 文字色を白
          */}
          <header className="p-4 bg-blue-500 text-white">
            {/*
              text-2xl : 文字サイズを 2XL (約1.5rem)
              font-bold: 太字
            */}
            <h1 className="text-2xl font-bold">ECサイトヘッダー</h1>
          </header>

          {/*
            [メインコンテンツ]
            p-4 : 全方向に1rem(16px)の余白
            {children} : 各ページごとの内容がここに表示される
          */}
          <main className="p-4">
            {children}
          </main>

          {/*
            [フッター]
            p-4          : パディング(16px)を適用
            bg-gray-800  : 背景色を濃いグレー
            text-white   : 文字色を白
            text-center  : 文字を中央揃え
          */}
          <footer className="p-4 bg-gray-800 text-white text-center">
            {/*
              &copy; : ©(コピーライト)マーク
              new Date().getFullYear() : 現在の年を取得
            */}
            &copy; {new Date().getFullYear()} ECサイト. All rights reserved.
          </footer>
        </ClientCartProvider>
      </body>
    </html>
  );
}