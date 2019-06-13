package com.checkers.model.messages;

import java.util.Arrays;

public class Message {
    private int gameId;
    private int userId;
    private int currentPlayer;
    private int myColor;        //value 0 - white | value 1 - black
    private int timeControl;
    private int timeControlBonus;
    private int winnerId;
    private String message;
    private String type;
    private String[][] board;


    public Message() {
    }

    public Message(int winnerId) {
        this.winnerId=winnerId;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public void setMyColor(int myColor) {
        this.myColor = myColor;
    }

    public int getTimeControl() {
        return timeControl;
    }

    public void setTimeControl(int timeControl) {
        this.timeControl = timeControl;
    }

    public int getTimeControlBonus() {
        return timeControlBonus;
    }

    public void setTimeControlBonus(int timeControlBonus) {
        this.timeControlBonus = timeControlBonus;
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
        StringBuilder string=new StringBuilder("Message{" +
                "gameId=" + gameId +
                ", userId=" + userId +
                ", currentPlayer=" + currentPlayer +
                ", myColor=" + myColor +
                ", message='" + message + '\'' +
                ", type='" + type + '\'' +
                ", board=\n");
        if(board!=null)
            for(String[] s:board)
                string.append(Arrays.toString(s)).append("\n");
        string.append('}');
        return string.toString();
    }
}
