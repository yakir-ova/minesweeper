package com.example.minesweeper.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class NewGameRequest {
    @NotNull
    @Min(2)
    public Integer rows;
    @NotNull
    @Min(2)
    public Integer cols;

    @NotNull
    @Min(1)
    public Integer mines;

    public Long seed;
    public Boolean firstClickSafe;
    public Boolean revealMinesOnGameOver;

    public NewGameRequest() {
        
    }
}
