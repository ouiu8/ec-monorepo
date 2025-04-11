# テストコードガイド

このプロジェクトのテストコードは、高品質なエンタープライズアプリケーションにおけるテストのベストプラクティスを示しています。このガイドでは、テストコードの重要な特徴と学ぶべき点を説明します。

## テスト設計の原則

1. **BDDスタイル (Given-When-Then)**: すべてのテストはBDD（振舞駆動開発）アプローチで記述されています。
   - **Given**: テスト前の準備、初期条件の設定
   - **When**: テスト対象の操作を実行
   - **Then**: 結果の検証と期待値との比較

2. **テストの論理的構造化**:
   - `@Nested`アノテーションを使用して、関連するテストをグループ化
   - 各テストクラスは特定のメソッドに焦点を当て、異なるケースを網羅
   - 命名規則は「~すべき」（shouldXxx）で統一し、テストの意図を明確化

3. **テストの網羅性**:
   - 正常系（成功パターン）と異常系（例外パターン）を網羅
   - エッジケースや境界値のテスト
   - 複雑なビジネスロジックの全パスをカバー

4. **隔離されたユニットテスト**:
   - モックを使用して外部依存（DB、他のサービス）を分離
   - 各テストが独立して実行可能

## 高度なテスト技法

### 1. モックの効果的な使用

```java
// BDDスタイルのモック設定
given(productDao.selectById(1L)).willReturn(product1);

// 戻り値の動的設定
given(orderDao.insert(any(Order.class))).willAnswer(invocation -> {
    Order order = invocation.getArgument(0);
    order.setId(1L);
    return 1;
});

// 例外をスローするモック
given(productDao.selectById(anyLong())).willThrow(new RuntimeException("DB error"));

// Voidメソッドのモック
willDoNothing().given(cartService).clearCart(SESSION_ID);
```

### 2. 引数キャプチャ（ArgumentCaptor）

引数キャプチャは、メソッドに渡された引数を検証するための強力な技術です。

```java
@Captor
private ArgumentCaptor<Order> orderCaptor;

// 使用方法
then(orderDao).should().insert(orderCaptor.capture());
Order capturedOrder = orderCaptor.getValue();
assertThat(capturedOrder.getUserId()).isEqualTo(USER_ID);

// 複数回呼び出された場合の全値取得
List<OrderItem> capturedItems = orderItemCaptor.getAllValues();
```

### 3. 高度なアサーション（AssertJ）

```java
// 複数の値を同時に検証
assertThat(result)
    .isNotNull()
    .satisfies(order -> {
        assertThat(order.getId()).isEqualTo(1L);
        assertThat(order.getUserId()).isEqualTo(USER_ID);
        assertThat(order.getStatus()).isEqualTo("PENDING");
    });

// コレクション内の複数要素を効率的に検証
assertThat(capturedProducts).hasSize(2)
    .extracting("id", "stock")
    .containsExactly(
        tuple(1L, 8),
        tuple(2L, 4)
    );
```

### 4. パラメータ化テスト

同じロジックを異なる入力値でテストする効率的な方法です。

```java
@ParameterizedTest
@ValueSource(ints = {-1, 0, 1000})
@DisplayName("境界値で商品数量を検証する")
void shouldValidateQuantityBoundaries(int quantity) {
    // テストロジック
}

@ParameterizedTest
@MethodSource("com.example.ecbackend.service.ProductServiceTest#invalidProductInputs")
@DisplayName("複数の無効な入力パターンで例外がスローされる")
void shouldThrowExceptionForVariousInvalidInputs(String name, Integer price, String expectedMessage) {
    // テストロジック
}

// メソッドソースの例
private static Stream<Arguments> invalidOrderInputs() {
    return Stream.of(
        Arguments.of(null, "住所", "CREDIT_CARD", "User ID is required"),
        Arguments.of(1L, "", "CREDIT_CARD", "Address cannot be empty")
    );
}
```

## テストデータの固定化

テストの再現性を確保するため、日時やIDなどを固定値にします。

```java
// テストで使用する固定日時
private final LocalDateTime fixedDateTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

// LocalDateTime.now()をモック化
try (var mockedStatic = mockStatic(LocalDateTime.class)) {
    mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);
    
    // テスト対象のメソッド呼び出し
    orderService.createOrderFromCart(SESSION_ID, USER_ID, SHIPPING_ADDRESS, PAYMENT_METHOD);
    
    // 検証
}
```

## テストヘルパーメソッド

テストコードの再利用性と可読性を高めるためのヘルパーメソッドを活用しています。

```java
private Order createSampleOrder(Long id) {
    Order order = new Order();
    order.setId(id);
    order.setUserId(USER_ID);
    // その他のプロパティ設定...
    return order;
}

private List<OrderItem> createSampleOrderItems(Long orderId) {
    // テスト用の注文明細作成ロジック
}
```

## 実装のポイント

これらのテストコードを参考にして、以下のポイントを意識してテストを実装しましょう：

1. **読みやすさを優先**: テストコードは仕様ドキュメントとしての役割も持ちます。
2. **一貫性のある命名**: メソッド名、変数名、コメントが一貫していると理解しやすいです。
3. **テストの独立性**: 各テストは他のテストに依存せず独立して実行できるようにします。
4. **モックの適切な使用**: 必要なものだけモック化し、過剰なモックは避けます。
5. **エッジケースのテスト**: 通常のケースだけでなく、境界値や異常系も忘れずテストします。
6. **リファクタリングを恐れない**: テストコードも通常のコードと同様に改善を続けましょう。

これらの原則とパターンに従うことで、保守性が高く、信頼性のあるテストコードを作成できます。 