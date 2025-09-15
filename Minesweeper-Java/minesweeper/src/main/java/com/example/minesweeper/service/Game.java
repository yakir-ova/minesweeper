package com.example.minesweeper.service;

import java.time.Instant;

final class Game {
    enum Status { PLAYING, WON, LOST }

    final int rows, cols, mines;
    final boolean firstClickSafe;
    final boolean revealMinesOnGameOver;
    final Cell[][] grid;

    Status status = Status.PLAYING;
    boolean initialized = false;
    int safeCellsRemaining;
    int flagsPlaced = 0;
    int moves = 0;

    final Instant startedAt = Instant.now();
    long elapsedMs = 0;

    Game(int rows, int cols, int mines, boolean firstClickSafe, boolean revealMinesOnGameOver) {
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.firstClickSafe = firstClickSafe;
        this.revealMinesOnGameOver = revealMinesOnGameOver;
        this.grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell();
            }
        }
        this.safeCellsRemaining = rows * cols - mines;
    }
}
