package com.checkers.models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class Piece implements Moveable{
    private final PieceColor color;
    private PieceType pieceType;
    private Place place;
    private ArrayList<Place> possibleMoves;

    public Piece(PieceColor color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
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

    public ArrayList<Place> getPossibleMoves() {
        return possibleMoves;
    }

    public void setPossibleMoves(ArrayList<Place> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }
}
