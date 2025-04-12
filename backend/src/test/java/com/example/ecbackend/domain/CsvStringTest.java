package com.example.ecbackend.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CsvString: CSVドメインのテスト")
class CsvStringTest {

    @ParameterizedTest
    @ValueSource(strings = {"test", "1,2,3", "a,b,c"})
    @DisplayName("通常の文字列で初期化する")
    void shouldCreateWithNormalString(String value) {
        // When: 文字列でCsvStringを初期化
        CsvString csvString = new CsvString(value);
        
        // Then: 値が正しく取得できる
        assertThat(csvString.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("空文字列やnullで初期化する")
    void shouldCreateWithEmptyOrNull(String value) {
        // When: 空文字列やnullでCsvStringを初期化
        CsvString csvString = new CsvString(value);
        
        // Then: 初期化に使用した値が取得できる
        assertThat(csvString.getValue()).isEqualTo(value);
    }
    
    @Test
    @DisplayName("カンマを含む文字列で初期化する")
    void shouldHandleCommaDelimitedString() {
        // Given: カンマを含む文字列
        String csvValue = "foo,bar,baz";
        
        // When: CSVドメインオブジェクトを作成
        CsvString csvString = new CsvString(csvValue);
        
        // Then: 値が正しく取得できる
        assertThat(csvString.getValue()).isEqualTo(csvValue);
    }
} 