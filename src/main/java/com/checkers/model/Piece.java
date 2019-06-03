package com.checkers.model;

public abstract class Piece implements Movable {
    private final PieceColor color;
    private PieceType pieceType;
    private Place place;

    Piece(PieceColor color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
    }

    Piece(PieceColor color) {
        this.color = color;
    }

    PieceColor getColor() {
        return color;
    }

    PieceType getPieceType() {
        return pieceType;
    }

    void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    Place getPlace() {
        return place;
    }

    void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (color != piece.color) return false;
        return place != null ? place.equals(piece.place) : piece.place == null;

    }

    @Override
    public int hashCode() {
        int result = color != null ? color.hashCode() : 0;
        result = 31 * result + (place != null ? place.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "color=" + color +
                ", pieceType=" + pieceType +
                ", place=" + place +
                '}';
    }
}
