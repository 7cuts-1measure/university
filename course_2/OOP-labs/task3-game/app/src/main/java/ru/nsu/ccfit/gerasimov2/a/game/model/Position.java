package ru.nsu.ccfit.gerasimov2.a.game.model;

public class Position {
    private int row;
    private int col;
    
    @Override
    public String toString() {
        return String.format("(%d, %d)",row, col);
    }

    public Position(Position other) {
        this.row = other.row;
        this.col = other.col;
    }

    public Position(int row, int col) {
        if (col < 0) throw new IllegalArgumentException("Invalid col (negative): " + col);
        if (row < 0) throw new IllegalArgumentException("Invalid row (negative): " + row);
        this.row = row;
        this.col = col;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        if (row < 0) throw new IllegalArgumentException("Invalid row to set (negative): " + row);
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        if (col< 0) throw new IllegalArgumentException("Invalid col to set (negative): " + col);
        this.col = col;
    }

    public boolean isSameAs(Position other) {
        return other != null && other.row == this.row && other.col == this.col;
    }

    
}
