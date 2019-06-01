package com.checkers.models;

import javax.persistence.criteria.CriteriaBuilder;

public class Message {
    private int gameId;
    private int userId;
    private String moveString;


    public Message() {
    }

    public Message(int gameId, int userId, String moveString) {
        this.gameId = gameId;
        this.userId = userId;
        this.moveString = moveString;
    }

    public Message(String stringToParse) {
        String[] s = stringToParse.split("/");
        this.gameId = Integer.parseInt(s[0]);
        this.userId = Integer.parseInt(s[1]);
        this.moveString = s[2];
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMoveString() {
        return moveString;
    }

    public void setMoveString(String moveString) {
        this.moveString = moveString;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
