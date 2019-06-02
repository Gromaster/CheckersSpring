package com.checkers.config;

import com.checkers.brokers.BoardStateEncoder;
import com.checkers.brokers.UserMoveDecoder;
import com.checkers.hibernate.util.ReaderDB;
import com.checkers.hibernate.util.SaverDB;
import com.checkers.models.Game;
import com.checkers.models.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@ServerEndpoint(
        value = "/game/{userId}",
        decoders = UserMoveDecoder.class,
        encoders = BoardStateEncoder.class )
public class GameEndpoint {
    private ReaderDB readerDB = new ReaderDB();
    private SaverDB saverDB = new SaverDB();
    private Session session;
    private static HashMap<Integer, GameEndpoint> gameEndpoints = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        gameEndpoints.put(userId,this);
    }

    @OnMessage
    public void onMessage(Session session, Message message){
        Game game;
        if((game= readerDB.load(message.getGameId()))==null)
            game = new Game(message.getGameId(),message.getUserId());
        else if(game.getBlackUser_id()==0){
            game.setBlackUser_id(message.getUserId());
        }
        else if (game.getWhiteUser_id()==0){
            game.setWhiteUser_id(message.getUserId());
        }

        try {
            game.makeMove(message.getMoveString());
            broadcast(game,message);
            game.switchPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            Message withoutChange = new Message();
            broadcast(game,message.eraseMovement());
        }
        saverDB.save(game);
    }



    private void broadcast(Game game,Message message) {
        try {
            gameEndpoints.get(game.getBlackUser_id()).session.getBasicRemote().sendObject(message);
            gameEndpoints.get(game.getWhiteUser_id()).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session){
    }

}

