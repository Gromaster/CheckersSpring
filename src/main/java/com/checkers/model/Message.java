package com.checkers.model;

import java.util.Date;

public class Message {
    private int gameId;
    private int userId;
    private int currentPlayer;
    private String moveString;
    private String[][] board;


    public Message() {
    }

    public Message(int gameId, int userId, int currentPlayer, String moveString) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.moveString = moveString;
    }

    public Message(int gameId, int userId, int currentPlayer, String moveString, String[][] board) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.moveString = moveString;
        this.board = board;
    }

    public Message(int gameId, int userId, int currentPlayer, String[][] board) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.board = board;
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
/*

    public Message eraseMovement(){
        String[] moveString = this.moveString.split("-");
        return new Message(this.gameId,this.userId,this.movementTime,String.format("%s-%s",moveString[0],moveString[0]));
    }
*/

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
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

    public void winner(int winner) {
        moveString = "win-" + winner;
    }

    @Override
    public String toString() {
        return "Message{" +
                "gameId=" + gameId +
                ", userId=" + userId +
                //", movementTime=" + movementTime +
                ", moveString='" + moveString + '\'' +
                '}';
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }
}
