package com.checkers.model.game;

public class Move {
    private Place origin;
    private Place destination;

    Move(Place origin, Place destination) {
        this.origin = origin;
        this.destination = destination;
    }

    Place getOrigin() {
        return origin;
    }

    public void setOrigin(Place origin) {
        this.origin = origin;
    }

    Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (!origin.equals(move.origin)) return false;
        return destination.equals(move.destination);

    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + destination.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Move{" +
                "origin=" + origin +
                ", destination=" + destination +
                '}';
    }

    public boolean isJump() {
        return Board.distance(origin, destination) > 1;
    }
}
