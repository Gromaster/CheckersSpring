package com.checkers.model.messages;

public class Message {
    private Integer gameId;
    private Integer userId;
    private Integer currentPlayer;
    private Integer myColor;        //value 0 - white | value 1 - black
    private String message;
    private String type;
    private String[][] board;


    public Message() {
    }

    public Message(int gameId, int userId, int myColor) {
        this.gameId = gameId;
        this.userId = userId;
        this.myColor = myColor;
    }

    public Message(int gameId, int userId, int currentPlayer, String message) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.message = message;
    }

    public Message(int gameId, int userId, int currentPlayer, String message, String[][] board) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.message = message;
        this.board = board;
    }

    public Message(int gameId, int userId, int currentPlayer, String[][] board) {
        this.gameId = gameId;
        this.userId = userId;
        this.currentPlayer = currentPlayer;
        this.board = board;
    }

    public Message(int gameId, int userId, String message) {
        this.gameId = gameId;
        this.userId = userId;
        this.message = message;
    }

    public Message(String stringToParse) {
        String[] s = stringToParse.split("/");
        this.gameId = Integer.parseInt(s[0]);
        this.userId = Integer.parseInt(s[1]);
        this.message = s[2];
    }

    public Message(int gameId, String message) {
        this.gameId = gameId;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public void winner(int winner) {
        message = "win-" + winner;
    }

    public String[][] getBoard() {
        return board;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public Integer getMyColor() {
        return myColor;
    }

    public void setMyColor(Integer myColor) {
        this.myColor = myColor;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCurrentPlayer(Integer currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "gameId=" + gameId +
                ", userId=" + userId +
                //", movementTime=" + movementTime +
                ", message='" + message + '\'' +
                '}';
    }

}
