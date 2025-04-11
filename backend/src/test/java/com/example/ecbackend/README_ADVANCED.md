# 高度なテスト手法ガイド

基本的なテスト手法に加えて、このドキュメントではより高度なテスト技術、自動化戦略、CI/CD統合について説明します。

## 高度なテスト戦略

### 1. ピラミッドテスト戦略

効果的なテスト戦略は「テストピラミッド」の概念に基づいています：

```
    ▲     UI/E2Eテスト (少数)
   ▲▲▲    インテグレーションテスト
 ▲▲▲▲▲▲▲  ユニットテスト (多数)
```

- **ユニットテスト**: 最も数が多く、実行が速く、信頼性が高い。個別のクラスやメソッドをテスト。
- **インテグレーションテスト**: コンポーネント間の相互作用をテスト。
- **UI/E2Eテスト**: エンドツーエンドでシステム全体の動作をテスト。

### 2. コントラクトテスト

マイクロサービスアーキテクチャでは、サービス間の契約を検証するコントラクトテストが重要です。

```java
@State("商品が存在する場合")
public void productExists() {
    // プロバイダー側のセットアップ
    given(productRepository.findById(42L)).willReturn(Optional.of(testProduct));
}

@Pact(consumer = "order-service", provider = "product-service")
public RequestResponsePact createPact(PactDslWithProvider builder) {
    // コントラクトを定義
    return builder
        .given("商品が存在する場合")
        .uponReceiving("商品情報のリクエスト")
            .path("/api/products/42")
            .method("GET")
        .willRespondWith()
            .status(200)
            .body(newJsonBody(body -> {
                body.stringType("name", "テスト商品");
                body.numberType("price", 1000);
            }).build())
        .toPact();
}
```

### 3. 変異テスト (Mutation Testing)

コードに意図的に欠陥を導入し、テストがそれを検出できるか確認する手法です。

```bash
# PITestを使用した変異テスト実行例
./gradlew pitest
```

### 4. プロパティベースのテスト

入力データの特性（プロパティ）に基づいて、自動的に多数のテストケースを生成する手法です。

```java
@Property
void allStringsReversedTwiceAreOriginal(
    @ForAll @StringLength(min = 0, max = 100) String original
) {
    String reversed = StringUtils.reverse(original);
    String reversedTwice = StringUtils.reverse(reversed);
    assertThat(reversedTwice).isEqualTo(original);
}
```

## 静的メソッドのモック化

静的メソッド（例：`LocalDateTime.now()`）をモック化する方法です：

```java
// Mockito 3.4.0以降で利用可能
try (var mockedStatic = mockStatic(LocalDateTime.class)) {
    mockedStatic.when(LocalDateTime::now).thenReturn(fixedDateTime);
    
    // テスト対象のメソッド呼び出し
    service.doSomething();
    
    // 検証
    mockedStatic.verify(LocalDateTime::now, times(1));
}
```

## テスト自動化とCI/CD統合

### 1. テストレポート生成

テスト実行結果を可視化するレポートを自動生成します：

```groovy
test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport // カバレッジレポート自動生成
}

jacocoTestReport {
    reports {
        xml.enabled true
        html.enabled true
    }
}
```

### 2. 並列テスト実行

テスト実行時間を短縮するための並列実行：

```groovy
test {
    maxParallelForks = Runtime.runtime.availableProcessors()
    forkEvery = 100 // 100テストごとにJVMを再起動
}
```

### 3. カスタムテストタグ

特定のカテゴリーのテストのみを実行する設定：

```java
@Tag("slow")
@Test
void timeConsumingTest() {
    // 時間のかかるテスト
}
```

```groovy
test {
    useJUnitPlatform {
        includeTags 'fast'
        excludeTags 'slow'
    }
}
```

### 4. SonarQubeとの統合

コード品質とテストカバレッジを継続的に監視：

```groovy
plugins {
    id "org.sonarqube" version "3.3"
}

sonarqube {
    properties {
        property "sonar.projectKey", "my-project"
        property "sonar.projectName", "My Project"
        property "sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml"
    }
}
```

## 高度なアサーション技法

### 1. ソフトアサーション

複数のアサーションが失敗しても、全てのチェックを実行：

```java
SoftAssertions softly = new SoftAssertions();
softly.assertThat(order.getId()).isEqualTo(1L);
softly.assertThat(order.getStatus()).isEqualTo("PENDING");
softly.assertThat(order.getTotalAmount()).isGreaterThan(0);
softly.assertAll(); // 全てのアサーションをまとめて実行
```

### 2. 動的テスト

実行時にテストケースを動的に生成：

```java
@TestFactory
Stream<DynamicTest> dynamicTestsFromCollection() {
    List<Order> orders = getTestOrders();
    
    return orders.stream()
        .map(order -> DynamicTest.dynamicTest(
            "注文検証: " + order.getId(),
            () -> {
                assertThat(order.getTotalAmount()).isPositive();
                assertThat(order.getStatus()).isNotNull();
            }
        ));
}
```

### 3. カスタムアサーション

ドメイン固有のアサーションメソッドを作成：

```java
public class OrderAssert extends AbstractAssert<OrderAssert, Order> {
    public static OrderAssert assertThat(Order actual) {
        return new OrderAssert(actual);
    }
    
    public OrderAssert hasValidStatus() {
        isNotNull();
        List<String> validStatuses = Arrays.asList("PENDING", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED");
        if (!validStatuses.contains(actual.getStatus())) {
            failWithMessage("Expected order to have valid status but was <%s>", actual.getStatus());
        }
        return this;
    }
    
    // 他のカスタムアサーションメソッド
}

// 使用例
OrderAssert.assertThat(order)
    .hasValidStatus()
    .satisfies(o -> {
        assertThat(o.getTotalAmount()).isPositive();
    });
```

これらの高度なテスト技法を適用することで、より堅牢で信頼性の高いテストスイートを構築できます。テストは単なるバグ検出だけでなく、設計改善や開発速度向上にも貢献します。 