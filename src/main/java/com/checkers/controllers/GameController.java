package com.checkers.controllers;

import com.checkers.hibernate.util.GameEntity;
import com.checkers.hibernate.util.GameResponse;
import com.checkers.hibernate.util.ReaderDB;
import com.checkers.hibernate.util.SaverDB;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    @MessageMapping("/game")
    @SendTo("/topic/game")
    public GameResponse getEntity(GameEntity gameEntity){
        SaverDB saverDB=new SaverDB();
        ReaderDB readerDB=new ReaderDB();
        saverDB.save(gameEntity);
        return new GameResponse(readerDB.load(gameEntity.getId()));/*new UserResponse("Hi " + user.getName())*/
    }

}
