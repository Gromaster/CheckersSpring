package com.checkers.models;

public class Move {
    private Place origin;
    private Place destination;

    public Move(Place origin, Place destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public Place getOrigin() {
        return origin;
    }

    public void setOrigin(Place origin) {
        this.origin = origin;
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }
}
