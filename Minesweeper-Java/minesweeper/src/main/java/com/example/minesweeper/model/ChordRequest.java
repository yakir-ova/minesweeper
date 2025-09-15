package com.example.minesweeper.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ChordRequest {
    @NotNull
    @Min(0)
    public Integer row;
    @NotNull
    @Min(0)
    public Integer col;

    public ChordRequest() {
        
    }
}
