package com.example.minesweeper.model;

public class GameState {
    private String gameId;
    private int rows;
    private int cols;
    private int mines;
    private String status;
    private StatsDTO stats;
    private CellDTO[][] cells;

    public GameState(String gameId, int rows, int cols, int mines, String status, StatsDTO stats, CellDTO[][] board) {
        this.gameId = gameId;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
        this.status = status;
        this.stats = stats;
        this.cells = board;
    }

    public String getGameId() {
        return gameId;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getMines() {
        return mines;
    }

    public String getStatus() {
        return status;
    }

    public StatsDTO getStats() {
        return stats;
    }

    public CellDTO[][] getCells() {
        return cells;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public void setStatus(String state) {
        this.status = state;
    }

    public void setStats(StatsDTO stats) {
        this.stats = stats;
    }

    public void setCells(CellDTO[][] cells) {
        this.cells = cells;
    }
}
