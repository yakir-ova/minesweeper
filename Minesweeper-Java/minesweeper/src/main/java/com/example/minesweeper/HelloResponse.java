package com.example.minesweeper;

public class HelloResponse {
    public String message;
    public long timestamp;

    public HelloResponse(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
}
