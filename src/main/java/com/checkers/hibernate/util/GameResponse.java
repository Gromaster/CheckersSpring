package com.checkers.hibernate.util;

import com.checkers.models.Game;

public class GameResponse {
    String content;

    public GameResponse() {
    }

    public GameResponse(String content) {
        this.content = content;
    }

    public GameResponse(Game game) {//TODO ma wysyłać całą planszę
        this.content = "Game id: " + game.getId() + "\t White user id: " + game.getWhiteUser_id() + "\t Black user id: " + game.getBlackUser_id();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
