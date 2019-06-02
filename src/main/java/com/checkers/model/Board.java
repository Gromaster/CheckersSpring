package com.checkers.model;

import java.util.ArrayList;

public class Board {
    private final int BOARD_SIZE = 32;
    private final int NUMBER_OF_ROWS = 8;
    private ArrayList<Place> places = new ArrayList<>(BOARD_SIZE);


    public Board() {
        initPlaces();
    }

    public Place getPlace(char column, int row) {
        return places.get(places.indexOf(new Place(column, row)));
    }

    private void initPlaces() {
        for (int i = 1; i <= NUMBER_OF_ROWS; i++) {
            for (int j = 0; j < (BOARD_SIZE / NUMBER_OF_ROWS); j++)
                places.add(new Place((char) (i % 2 == 1 ? ('A' + 2 * j) : ('B' + 2 * j)), i));
        }
        if (places.size() != 32) throw new RuntimeException("Places initialization failed, illegal number of places");
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPieceOnPlaces(Place place, Piece piece) {
        this.places.get(places.indexOf(place)).setPieceOccupying(piece);
    }

    public Place getPlace(Place place) {
        return places.get(places.indexOf(place));
    }

    public Place placeBefore(Place place1, Place place2) {
        int vectorP1P2row = place2.getRow() - place1.getRow();
        int vectorP1P2column = (int) place2.getColumn() - (int) place1.getColumn();
        return getPlace((char) ((int) place2.getColumn() - (int) Math.signum(vectorP1P2column)), place2.getRow() - (int) Math.signum(vectorP1P2row));
    }

    public int distance(Place origin, Place destination) {

        return Math.max(Math.abs(origin.getColumn() - destination.getColumn()), Math.abs(origin.getRow() - destination.getRow()));
    }
}
