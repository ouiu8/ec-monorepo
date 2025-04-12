package com.example.ecbackend.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Result: 汎用結果モデルのテスト")
class ResultTest {

    @Test
    @DisplayName("データのみの結果オブジェクト生成")
    void shouldCreateResultWithDataOnly() {
        // Given: テスト用のデータとステータス
        String data = "テストデータ";
        int status = 200;
        
        // When: データのみの結果オブジェクトを生成
        Result<String> result = Result.of(status, data);
        
        // Then: データとステータスが正しく設定され、メッセージがnull
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.getMessage()).isNull();
    }
    
    @Test
    @DisplayName("データとメッセージを含む結果オブジェクト生成")
    void shouldCreateResultWithDataAndMessage() {
        // Given: テスト用のデータ、ステータス、メッセージ
        Integer data = 42;
        int status = 201;
        String message = "作成完了";
        
        // When: データとメッセージを含む結果オブジェクトを生成
        Result<Integer> result = Result.of(status, data, message);
        
        // Then: データ、ステータス、メッセージが正しく設定される
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.getMessage()).isEqualTo(message);
    }
    
    @Test
    @DisplayName("エラー結果オブジェクト生成")
    void shouldCreateErrorResult() {
        // Given: エラーステータスとメッセージ
        int status = 404;
        String message = "リソースが見つかりません";
        
        // When: データ部分がnullのエラー結果オブジェクトを生成
        Result<Object> result = Result.of(status, null, message);
        
        // Then: ステータスとメッセージが正しく設定され、データがnull
        assertThat(result.getStatus()).isEqualTo(status);
        assertThat(result.getData()).isNull();
        assertThat(result.getMessage()).isEqualTo(message);
    }
} 