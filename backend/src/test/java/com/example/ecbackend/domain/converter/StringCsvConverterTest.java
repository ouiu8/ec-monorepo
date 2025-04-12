package com.example.ecbackend.domain.converter;

import com.example.ecbackend.domain.CsvString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringCsvConverter: CSVドメインコンバーターのテスト")
class StringCsvConverterTest {

    private final StringCsvConverter converter = new StringCsvConverter();

    @ParameterizedTest
    @ValueSource(strings = {"test", "1,2,3", "a,b,c"})
    @DisplayName("文字列からドメインオブジェクトへの変換")
    void fromValueToDomain(String value) {
        // When: 文字列からドメインオブジェクトに変換
        CsvString domain = converter.fromValueToDomain(value);
        
        // Then: 正しく変換されていること
        assertThat(domain).isNotNull();
        assertThat(domain.getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("空文字列や空白文字からドメインオブジェクトへの変換")
    void fromValueToDomainWithEmptyOrBlank(String value) {
        // When: 空文字列や空白文字からドメインオブジェクトに変換
        CsvString domain = converter.fromValueToDomain(value);
        
        // Then: nullの場合はnull、それ以外は元の値が保持されていること
        assertThat(domain).isNotNull();
        assertThat(domain.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("ドメインオブジェクトから文字列への変換")
    void fromDomainToValue() {
        // Given: テスト用のドメインオブジェクト
        String original = "a,b,c,d";
        CsvString domain = new CsvString(original);
        
        // When: ドメインオブジェクトから文字列に変換
        String result = converter.fromDomainToValue(domain);
        
        // Then: 正しく変換されていること
        assertThat(result).isEqualTo(original);
    }
} 