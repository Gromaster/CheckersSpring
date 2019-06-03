package com.checkers.model;

import java.util.ArrayList;

public abstract class Piece implements Movable {
    private final PieceColor color;
    private PieceType pieceType;
    private Place place;

    public Piece(PieceColor color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
    }

    public Piece(PieceColor color) {
        this.color = color;
    }


    public PieceColor getColor() {
        return color;
    }

    public PieceType getPieceType() {
        return pieceType;
    }

    public void setPieceType(PieceType pieceType) {
        this.pieceType = pieceType;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
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
