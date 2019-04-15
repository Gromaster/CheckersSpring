package com.checkers.hibernate.util;

public class GameResponse {
    String content;

    public GameResponse(){
    }

    public GameResponse(GameEntity gameEntity) {
        this.content = "Game id: "+gameEntity.getId()+"\t White user id: "+gameEntity.getWhiteUser_id()+"\t Black user id: "+gameEntity.getBlackUser_id();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
