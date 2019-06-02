package com.checkers.model;

import java.util.Date;

public class Message {
    private int gameId;
    private int userId;
    private Date movementTime;
    private String moveString;



    public Message() {
    }

    public Message(int gameId, int userId, Date movementTime, String moveString) {
        this.gameId = gameId;
        this.userId = userId;
        this.movementTime = movementTime;
        this.moveString = moveString;
    }

    public Message(String stringToParse) {
        String[] s = stringToParse.split("/");
        this.gameId = Integer.parseInt(s[0]);
        this.userId = Integer.parseInt(s[1]);
        this.moveString = s[2];
    }

    public Message eraseMovement(){
        String[] moveString = this.moveString.split("-");
        return new Message(this.gameId,this.userId,this.movementTime,String.format("%s-%s",moveString[0],moveString[0]));
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

    public Date getMovementTime() {
        return movementTime;
    }

    public void setMovementTime(Date movementTime) {
        this.movementTime = movementTime;
    }
}
