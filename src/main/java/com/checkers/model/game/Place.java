package com.checkers.model.game;


import com.sun.istack.NotNull;

public class Place {
    private final char column;
    private final int row;
    private Piece pieceOccupying = null;
    private PieceColor colorOfPieceOccupying = null;


    Place(char column, int row) {
        this.column = column;
        if (row > 40) row -= 48;
        this.row = row;
    }

    Place(String s) {
        this(s.charAt(0), Integer.parseInt(s.substring(1)));
    }

    Piece getPieceOccupying() {
        return pieceOccupying;
    }

    void setPieceOccupying(@NotNull Piece pieceOccupying) {
        pieceOccupying.setPlace(this);
        this.pieceOccupying = pieceOccupying;
    }

    char getColumn() {
        return column;
    }

    int getRow() {
        return row;
    }

    boolean isOutOfBoard() {
        if (column < 'A' || column > 'H') return true;
        return row < 1 || row > 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return column == place.column && row == place.row;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result *= prime * (int) column;
        result *= prime * row;
        return result;
    }

    @Override
    public String toString() {
        return column + Integer.toString(row);
    }

    void free() {
        this.pieceOccupying = null;
    }

    public boolean isEmpty() {
        return pieceOccupying == null;
    }
}
