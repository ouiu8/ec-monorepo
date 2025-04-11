package com.example.ecbackend.service;

import com.example.ecbackend.dao.ProductDao;
import com.example.ecbackend.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/**
 * ProductServiceのユニットテスト
 * 
 * テスト設計原則:
 * 1. 各メソッドの正常系と異常系を網羅
 * 2. モックを使用してDAOレイヤーを分離
 * 3. BDDスタイル（given-when-then）でテスト構造を明確に
 * 4. ビジネスロジックの正確な実行を検証
 * 5. パラメータ化テストで多様な入力値をテスト
 * 6. エッジケースと境界値の徹底的検証
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService: 商品ビジネスロジックのテスト")
class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductService productService;
    
    // 時間固定のためのモックタイム
    private final LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

    // テスト用データ生成ヘルパーメソッド
    private Product createTestProduct(Long id, String name, String description, Integer price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(100);
        product.setImageUrl("http://example.com/image.jpg");
        product.setCreatedAt(fixedTime.minusDays(1));
        product.setUpdatedAt(fixedTime.minusDays(1));
        return product;
    }
    
    /**
     * 無効な入力値のソースメソッド
     */
    private static Stream<Arguments> invalidProductInputs() {
        return Stream.of(
            // name, price, expectedExceptionMessage
            Arguments.of(null, 1000, "Product name cannot be empty"),
            Arguments.of("", 1000, "Product name cannot be empty"),
            Arguments.of("  ", 1000, "Product name cannot be empty"),
            Arguments.of("Product Name", -1, "Product price must be a non-negative value"),
            Arguments.of("Product Name", null, "Product price must be a non-negative value")
        );
    }

    @Nested
    @DisplayName("getAllProducts(): 全商品取得")
    class GetAllProductsTests {

        @Test
        @DisplayName("全商品リストを取得できる")
        void shouldReturnAllProductsFromDao() {
            // Given: 複数の商品が存在する場合
            List<Product> expectedProducts = Arrays.asList(
                createTestProduct(1L, "テスト商品1", "説明1", 1000),
                createTestProduct(2L, "テスト商品2", "説明2", 2000),
                createTestProduct(3L, "テスト商品3", "説明3", 3000)
            );
            
            given(productDao.selectAll()).willReturn(expectedProducts);

            // When: 全商品を取得
            List<Product> actualProducts = productService.getAllProducts();

            // Then: 期待通りの商品リストが返される
            assertThat(actualProducts)
                .isNotNull()
                .hasSize(3)
                .isEqualTo(expectedProducts)
                .extracting("id", "name", "price")
                .containsExactly(
                    tuple(1L, "テスト商品1", 1000),
                    tuple(2L, "テスト商品2", 2000),
                    tuple(3L, "テスト商品3", 3000)
                );
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectAll();
            then(productDao).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("商品がない場合は空リストを返す")
        void shouldReturnEmptyListWhenNoProducts() {
            // Given: 商品が存在しない場合
            given(productDao.selectAll()).willReturn(Collections.emptyList());

            // When: 全商品を取得
            List<Product> actualProducts = productService.getAllProducts();

            // Then: 空のリストが返される
            assertThat(actualProducts)
                .isNotNull()
                .isEmpty();
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectAll();
            then(productDao).shouldHaveNoMoreInteractions();
        }
        
        @Test
        @DisplayName("DAOで例外が発生した場合はそのまま伝播される")
        void shouldPropagateExceptionFromDao() {
            // Given: DAOで例外が発生する場合
            String errorMessage = "Database connection error";
            given(productDao.selectAll()).willThrow(new RuntimeException(errorMessage));
            
            // When & Then: サービスから同じ例外が伝播される
            assertThatThrownBy(() -> productService.getAllProducts())
                .isInstanceOf(RuntimeException.class)
                .hasMessage(errorMessage);
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectAll();
            then(productDao).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("getProductById(): 商品ID検索")
    class GetProductByIdTests {

        @Test
        @DisplayName("存在するIDで商品を取得できる")
        void shouldReturnProductWhenIdExists() {
            // Given: 指定IDの商品が存在する場合
            Long productId = 1L;
            Product expectedProduct = createTestProduct(productId, "テスト商品", "商品説明", 1500);
            
            given(productDao.selectById(productId)).willReturn(expectedProduct);

            // When: 商品IDで検索
            Product actualProduct = productService.getProductById(productId);

            // Then: 期待する商品が返される
            assertThat(actualProduct)
                .isNotNull()
                .isEqualTo(expectedProduct)
                .satisfies(product -> {
                    assertThat(product.getId()).isEqualTo(productId);
                    assertThat(product.getName()).isEqualTo("テスト商品");
                    assertThat(product.getDescription()).isEqualTo("商品説明");
                    assertThat(product.getPrice()).isEqualTo(1500);
                });
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectById(productId);
            then(productDao).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("存在しないIDでは例外がスローされる")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            // Given: 指定IDの商品が存在しない場合
            Long nonExistentId = 999L;
            given(productDao.selectById(nonExistentId)).willReturn(null);

            // When & Then: NoSuchElementExceptionがスローされる
            assertThatThrownBy(() -> productService.getProductById(nonExistentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Product not found with id: " + nonExistentId);
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectById(nonExistentId);
            then(productDao).shouldHaveNoMoreInteractions();
        }
        
        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -999L})
        @DisplayName("無効なIDでは例外がスローされる可能性がある")
        void shouldPotentiallyThrowExceptionForInvalidIds(Long invalidId) {
            // Given: Mockの設定（実装によってはnullを返すか例外を返す可能性がある）
            given(productDao.selectById(invalidId)).willReturn(null);
            
            // When & Then: serviceでは存在しない商品として処理される
            assertThatThrownBy(() -> productService.getProductById(invalidId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Product not found with id: " + invalidId);
                
            // DAOが正確に呼び出されたことを検証
            then(productDao).should(times(1)).selectById(invalidId);
            then(productDao).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("updateProduct(): 商品更新")
    class UpdateProductTests {

        @Test
        @DisplayName("商品を正常に更新できる")
        void shouldUpdateProduct() {
            // Given: 既存の商品が存在する場合
            Long productId = 1L;
            Product existingProduct = createTestProduct(productId, "既存商品", "既存の説明", 1000);
            Product updatedProduct = createTestProduct(productId, "更新商品", "更新された説明", 2000);
            updatedProduct.setStock(50);
            updatedProduct.setImageUrl("http://example.com/updated.jpg");
            
            // モック設定
            given(productDao.selectById(productId)).willReturn(existingProduct);
            given(productDao.update(any(Product.class))).willReturn(1);
            
            // シミュレートする現在時刻
            LocalDateTime now = fixedTime;
            
            // インスタンスコピーを作成して、最終状態を正確に検証するため
            Product expectedProduct = new Product();
            expectedProduct.setId(productId);
            expectedProduct.setName("更新商品");
            expectedProduct.setDescription("更新された説明");
            expectedProduct.setPrice(2000);
            expectedProduct.setStock(50);
            expectedProduct.setImageUrl("http://example.com/updated.jpg");
            expectedProduct.setCreatedAt(existingProduct.getCreatedAt()); // 作成日時は変更なし
            expectedProduct.setUpdatedAt(now); // 更新日時のみ更新
            
            // テスト対象メソッド内でLocalDateTime.nowが呼ばれるため、一時的にモックする
            try (var mockedStatic = mockStatic(LocalDateTime.class)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(now);
                
                // When: 商品を更新
                Product result = productService.updateProduct(updatedProduct);
                
                // Then: 商品が適切に更新される
                assertThat(result)
                    .isNotNull()
                    .satisfies(product -> {
                        assertThat(product.getId()).isEqualTo(productId);
                        assertThat(product.getName()).isEqualTo("更新商品");
                        assertThat(product.getDescription()).isEqualTo("更新された説明");
                        assertThat(product.getPrice()).isEqualTo(2000);
                        assertThat(product.getStock()).isEqualTo(50);
                        assertThat(product.getImageUrl()).isEqualTo("http://example.com/updated.jpg");
                        // 作成日時はそのまま、更新日時のみ更新
                        assertThat(product.getCreatedAt()).isEqualTo(existingProduct.getCreatedAt());
                        assertThat(product.getUpdatedAt()).isEqualTo(now);
                    });
                
                // DAOの呼び出しを検証
                ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
                then(productDao).should(times(1)).selectById(productId);
                then(productDao).should(times(1)).update(productCaptor.capture());
                
                // 更新するProductオブジェクトの内容を検証
                Product capturedProduct = productCaptor.getValue();
                assertThat(capturedProduct)
                    .usingRecursiveComparison()
                    .isEqualTo(expectedProduct);
            }
        }

        @Test
        @DisplayName("nullの商品で更新を試みると例外がスローされる")
        void shouldThrowExceptionWhenProductIsNull() {
            // Given: 更新する商品がnull
            Product nullProduct = null;
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> productService.updateProduct(nullProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("IDがnullの商品で更新を試みると例外がスローされる")
        void shouldThrowExceptionWhenProductIdIsNull() {
            // Given: IDがnullの商品
            Product productWithNullId = createTestProduct(null, "テスト商品", "説明", 1000);
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> productService.updateProduct(productWithNullId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product ID cannot be null");
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
        
        @Test
        @DisplayName("存在しない商品IDで更新を試みると例外がスローされる")
        void shouldThrowExceptionWhenProductDoesNotExist() {
            // Given: 存在しない商品ID
            Long nonExistentId = 999L;
            Product productWithNonExistentId = createTestProduct(nonExistentId, "存在しない商品", "説明", 1000);
            
            // 存在しない商品IDの場合はnullが返される
            given(productDao.selectById(nonExistentId)).willReturn(null);
            
            // When & Then: NoSuchElementExceptionがスローされる
            assertThatThrownBy(() -> productService.updateProduct(productWithNonExistentId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Product not found with id: " + nonExistentId);
                
            // selectByIdは呼び出されるが、updateは呼び出されないことを検証
            then(productDao).should(times(1)).selectById(nonExistentId);
            then(productDao).should(never()).update(any(Product.class));
        }
        
        @Test
        @DisplayName("更新処理が失敗すると例外がスローされる")
        void shouldThrowExceptionWhenUpdateFails() {
            // Given: 既存の商品があるが、更新処理が失敗する場合
            Long productId = 1L;
            Product existingProduct = createTestProduct(productId, "既存商品", "既存の説明", 1000);
            Product updatedProduct = createTestProduct(productId, "更新商品", "更新された説明", 2000);
            
            // モック設定
            given(productDao.selectById(productId)).willReturn(existingProduct);
            // 更新時に0が返される（= 更新失敗）
            given(productDao.update(any(Product.class))).willReturn(0);
            
            // When & Then: RuntimeExceptionがスローされる
            assertThatThrownBy(() -> productService.updateProduct(updatedProduct))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to update product with id: " + productId);
                
            // DAOの呼び出しを検証
            then(productDao).should(times(1)).selectById(productId);
            then(productDao).should(times(1)).update(any(Product.class));
        }
    }
    
    @Nested
    @DisplayName("createProduct(): 商品登録")
    class CreateProductTests {
    
        @Test
        @DisplayName("新規商品を登録できる")
        void shouldCreateNewProduct() {
            // Given: 有効な商品情報
            Product newProduct = new Product();
            newProduct.setName("新商品");
            newProduct.setDescription("新商品の説明");
            newProduct.setPrice(1500);
            newProduct.setStock(100);
            newProduct.setImageUrl("http://example.com/new.jpg");
            
            // モック設定
            given(productDao.insert(any(Product.class))).willReturn(1);
            
            // シミュレートする現在時刻
            LocalDateTime now = fixedTime;
            
            // テスト対象メソッド内でLocalDateTime.nowが呼ばれるため、一時的にモックする
            try (var mockedStatic = mockStatic(LocalDateTime.class)) {
                mockedStatic.when(LocalDateTime::now).thenReturn(now);
                
                // When: 新規商品を登録
                Product result = productService.createProduct(newProduct);
                
                // Then: 商品が適切に登録される
                assertThat(result)
                    .isNotNull()
                    .isSameAs(newProduct)
                    .satisfies(product -> {
                        assertThat(product.getName()).isEqualTo("新商品");
                        assertThat(product.getDescription()).isEqualTo("新商品の説明");
                        assertThat(product.getPrice()).isEqualTo(1500);
                        assertThat(product.getStock()).isEqualTo(100);
                        assertThat(product.getImageUrl()).isEqualTo("http://example.com/new.jpg");
                        // 日時が設定されていることを確認
                        assertThat(product.getCreatedAt()).isEqualTo(now);
                        assertThat(product.getUpdatedAt()).isEqualTo(now);
                    });
                
                // DAOが適切に呼び出されたことを検証
                ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
                then(productDao).should(times(1)).insert(productCaptor.capture());
                
                // insertされたProductオブジェクトの内容を検証
                Product capturedProduct = productCaptor.getValue();
                assertThat(capturedProduct).isSameAs(newProduct);
                assertThat(capturedProduct.getCreatedAt()).isEqualTo(now);
                assertThat(capturedProduct.getUpdatedAt()).isEqualTo(now);
            }
        }
        
        @Test
        @DisplayName("nullの商品で登録を試みると例外がスローされる")
        void shouldThrowExceptionWhenProductIsNull() {
            // Given: 登録する商品がnull
            Product nullProduct = null;
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> productService.createProduct(nullProduct))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product cannot be null");
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
        
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        @DisplayName("商品名が空の場合は例外がスローされる")
        void shouldThrowExceptionWhenNameIsEmpty(String invalidName) {
            // Given: 商品名が空の商品
            Product productWithEmptyName = new Product();
            productWithEmptyName.setName(invalidName);
            productWithEmptyName.setPrice(1000);
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> productService.createProduct(productWithEmptyName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be empty");
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
        
        @ParameterizedTest
        @ValueSource(ints = {-1, -1000})
        @DisplayName("価格が負の値の場合は例外がスローされる")
        void shouldThrowExceptionWhenPriceIsNegative(int invalidPrice) {
            // Given: 価格が負の商品
            Product productWithNegativePrice = new Product();
            productWithNegativePrice.setName("テスト商品");
            productWithNegativePrice.setPrice(invalidPrice);
            
            // When & Then: IllegalArgumentExceptionがスローされる
            assertThatThrownBy(() -> productService.createProduct(productWithNegativePrice))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product price must be a non-negative value");
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
        
        @Test
        @DisplayName("登録処理が失敗すると例外がスローされる")
        void shouldThrowExceptionWhenInsertFails() {
            // Given: 有効な商品情報だが、登録処理が失敗する場合
            Product validProduct = new Product();
            validProduct.setName("テスト商品");
            validProduct.setDescription("説明");
            validProduct.setPrice(1000);
            
            // モック設定
            given(productDao.insert(any(Product.class))).willReturn(0);
            
            // When & Then: RuntimeExceptionがスローされる
            assertThatThrownBy(() -> productService.createProduct(validProduct))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to create product");
                
            // DAOが適切に呼び出されたことを検証
            then(productDao).should(times(1)).insert(any(Product.class));
        }
        
        @ParameterizedTest
        @MethodSource("com.example.ecbackend.service.ProductServiceTest#invalidProductInputs")
        @DisplayName("複数の無効な入力パターンで例外がスローされる")
        void shouldThrowExceptionForVariousInvalidInputs(String name, Integer price, String expectedMessage) {
            // Given: 無効な商品情報
            Product invalidProduct = new Product();
            invalidProduct.setName(name);
            invalidProduct.setPrice(price);
            
            // When: 例外をキャッチ
            Throwable thrown = catchThrowable(() -> productService.createProduct(invalidProduct));
            
            // Then: 期待通りの例外と例外メッセージ
            assertThat(thrown)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
                
            // DAOが呼び出されないことを検証
            then(productDao).shouldHaveNoInteractions();
        }
    }
} 