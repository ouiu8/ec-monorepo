package com.example.ecbackend.model;

public class Result<T> {
    private final int status;
    private final T data;
    private final String message;

    private Result(int status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Result<T> of(int status, T data) {
        return new Result<>(status, data, null);
    }

    public static <T> Result<T> of(int status, T data, String message) {
        return new Result<>(status, data, message);
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
} 