package com.example.minesweeper.model;

public class CellDTO {
    private boolean revealed = false;
    private boolean flagged = false;
    private int adjacent = 0;
    private boolean mine = false;

    public CellDTO(boolean revealed, boolean flagged, int adjacent, boolean mine) {
        this.revealed = revealed;
        this.flagged = flagged;
        this.adjacent = adjacent;
        this.mine = mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public int getAdjacent() {
        return adjacent;
    }

    public void setAdjacent(int adjacent) {
        this.adjacent = adjacent;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

}
