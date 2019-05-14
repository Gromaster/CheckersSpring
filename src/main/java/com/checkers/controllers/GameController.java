package com.checkers.controllers;

import com.checkers.hibernate.util.GameEntity;
import com.checkers.hibernate.util.GameResponse;
import com.checkers.hibernate.util.ReaderDB;
import com.checkers.hibernate.util.SaverDB;
import com.checkers.models.Board;
import com.checkers.models.Game;
import com.checkers.models.UserMove;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.websocket.OnOpen;


@Controller
public class GameController {
    SaverDB saverDB=new SaverDB();
    ReaderDB readerDB=new ReaderDB();
    Game game;

    @OnOpen
    @SendTo("/topic/game")
    public GameResponse opening(UserMove userConnection){

        readerDB.load(userConnection.getGameId());



        return new GameResponse("Did it again");
    }


    @MessageMapping("/game")
    @SendTo("/topic/game")
    public GameResponse getEntity(UserMove userMove){
        game = readerDB.load(userMove.getGameId());
        if(userMove.getUserId()==game.getCurrentPlayerId())
            game.makeMove(userMove.getMoveString());

        saverDB.save(game);
        return new GameResponse(game);
    }

}
