package com.checkers.models;


public class Place {
    private final char column;
    private final int row;
    private Piece pieceOccupying = null;
    private PieceColor colorOfPieceOccupying = null;


    public Place(char column, int row) {
        this.column = column;
        this.row = row;
    }

    public Place(String s) {
        this(s.charAt(0), s.charAt(1));
    }

    public PieceColor getColorOfPieceOccupying() {
        return colorOfPieceOccupying;
    }

    public void setColorOfPieceOccupying(PieceColor colorOfPieceOccupying) {
        if (this.colorOfPieceOccupying != null) throw new RuntimeException("Trying to place one piece over another");
        this.colorOfPieceOccupying = colorOfPieceOccupying;
    }

    public Piece getPieceOccupying() {
        return pieceOccupying;
    }

    public void setPieceOccupying(Piece pieceOccupying) {
        pieceOccupying.setPlace(this);
        this.pieceOccupying = pieceOccupying;
    }

    public char getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean isOutOfBoard() {
        if (column < 'A' || column > 'H') return true;
        if (row < 1 || row > 8) return true;
        return false;
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

    public void free() {
        this.pieceOccupying = null;
    }

    public boolean isEmpty() {
        return pieceOccupying == null;
    }
}
