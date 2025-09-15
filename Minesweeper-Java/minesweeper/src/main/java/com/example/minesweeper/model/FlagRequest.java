package com.example.minesweeper.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class FlagRequest {
    @NotNull
    @Min(0)
    public Integer row;
    @NotNull
    @Min(0)
    public Integer col;

    public Boolean flagged;

    public FlagRequest() {
        
    }
}
