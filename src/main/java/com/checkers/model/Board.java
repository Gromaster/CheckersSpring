package com.checkers.model;

import java.util.ArrayList;

public class Board {
    private final int BOARD_SIZE = 32;
    private ArrayList<Place> places = new ArrayList<>(BOARD_SIZE);


    Board() {
        initPlaces();
    }

    private Place getPlace(char column, int row) {
        return places.get(places.indexOf(new Place(column, row)));
    }

    private void initPlaces() {
        int numberOfRows = 8;
        for (int i = 1; i <= numberOfRows; i++) {
            for (int j = 0; j < (BOARD_SIZE / numberOfRows); j++)
                places.add(new Place((char) (i % 2 == 1 ? ('A' + 2 * j) : ('B' + 2 * j)), i));
        }
        if (places.size() != 32) throw new RuntimeException("Places initialization failed, illegal number of places");
    }

    ArrayList<Place> getPlaces() {
        return places;
    }

    void emptyPlace(Place place) {
        this.places.get(places.indexOf(place)).free();
    }

    Place getPlace(Place place) {
        return places.get(places.indexOf(place));
    }

    Place placeBefore(Place place1, Place place2) {
        int vectorP1P2row = place2.getRow() - place1.getRow();
        int vectorP1P2column = (int) place2.getColumn() - (int) place1.getColumn();
        return getPlace((char) ((int) place2.getColumn() - (int) Math.signum(vectorP1P2column)), place2.getRow() - (int) Math.signum(vectorP1P2row));
    }

    static int distance(Place origin, Place destination) {
        return Math.max(Math.abs(origin.getColumn() - destination.getColumn()), Math.abs(origin.getRow() - destination.getRow()));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Place place : places) {
            str.append(place.toString());
            if (place.getPieceOccupying() == null) str.append("no piece");
            else str.append(place.getPieceOccupying().toString());
        }
        return str.toString();
    }
}
