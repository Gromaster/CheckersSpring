package com.checkers.hibernate.util;

import com.checkers.model.Game;

public class GameResponse {
    private String content;

    public GameResponse() {
    }

    public GameResponse(String content) {
        this.content = content;
    }

    public GameResponse(Game game) {
        this.content = "Game id: " + game.getId() + "\t White user id: " + game.getWhiteUser_id() + "\t Black user id: " + game.getBlackUser_id();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
