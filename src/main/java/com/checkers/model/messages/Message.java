package com.checkers.model.messages;

import java.util.Date;

public class Message {
    private int gameId;
    private int userId;
    private int currentPlayer;
    private String moveString;
    private String[][] board;
    private ChatMessage message;


    public Message() {
    }



    public Message(int gameId, int userId, ChatMessage message) {
        this.gameId = gameId;
        this.userId = userId;
        this.message = message;
    }

    public Message(int gameId, int userId, int currentPlayer, String moveString, String[][] board, ChatMessage message) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.moveString = moveString;
        this.board = board;
        this.message = message;
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

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
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

}
