package com.example.ecbackend.exception;

import com.example.ecbackend.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

/**
 * アプリケーション全体の例外をハンドリングするグローバル例外ハンドラー
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * リクエストヘッダーが不足している場合のハンドラー
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Result<String>> handleMissingRequestHeader(MissingRequestHeaderException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.of(HttpStatus.BAD_REQUEST.value(), null, "必須のヘッダーが不足しています: " + ex.getHeaderName()));
    }

    /**
     * パスパラメータなどの型変換エラーのハンドラー
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.of(HttpStatus.BAD_REQUEST.value(), null, "パラメータの型が不正です: " + ex.getName()));
    }

    /**
     * 要素が見つからない場合のハンドラー
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Result<String>> handleNoSuchElement(NoSuchElementException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Result.of(HttpStatus.NOT_FOUND.value(), null, ex.getMessage()));
    }

    /**
     * 不正な引数が提供された場合のハンドラー
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.of(HttpStatus.BAD_REQUEST.value(), null, ex.getMessage()));
    }

    /**
     * セキュリティ関連の例外のハンドラー
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Result<String>> handleSecurityException(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.of(HttpStatus.FORBIDDEN.value(), null, "この操作を実行する権限がありません"));
    }

    /**
     * その他の予期しない例外のハンドラー
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<String>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "サーバー内部エラーが発生しました"));
    }

    /**
     * システムエラー（Error）のハンドラー
     */
    @ExceptionHandler(Error.class)
    public ResponseEntity<Result<String>> handleError(Error error) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "重大なシステムエラーが発生しました"));
    }
} 