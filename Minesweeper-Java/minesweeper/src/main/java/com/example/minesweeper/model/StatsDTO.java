package com.example.minesweeper.model;

public class StatsDTO {
    private int flagsPlaced = 0;
    private int safeCellsRemaining = 85;
    private int moves = 0;
    private String startedAt;
    private long elapsedMs = 0;

    public StatsDTO(int flagsPlaced, int safeCellsRemaining, int moves, String startedAt, long elapsedMs) {
        this.flagsPlaced = flagsPlaced;
        this.safeCellsRemaining = safeCellsRemaining;
        this.moves = moves;
        this.startedAt = startedAt;
        this.elapsedMs = elapsedMs;
    }

    public int getFlagsPlaced() {
        return flagsPlaced;
    }

    public void setFlagsPlaced(int flagsPlaced) {
        this.flagsPlaced = flagsPlaced;
    }

    public int getSafeCellsRemaining() {
        return safeCellsRemaining;
    }

    public void setSafeCellsRemaining(int safeCellsRemaining) {
        this.safeCellsRemaining = safeCellsRemaining;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public long getElapsedMs() {
        return elapsedMs;
    }

    public void setElapsedMs(long elapsedTime) {
        this.elapsedMs = elapsedTime;
    }
}
