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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint(
        value = "/game/{userId}",
        decoders = UserMoveDecoder.class,
        encoders = BoardStateEncoder.class )
public class GameEndpoint {
    private ReaderDB readerDB;
    private SaverDB saverDB;
    private Session session;
    private static List<GameEndpoint> gameEndpointList = new CopyOnWriteArrayList<>();
    private static HashMap<String, Integer> users = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        gameEndpointList.add(this);
        users.put(session.getId(), userId);
        checkDB4Game(userId);
        Game game = new Game();//TODO sprawdzanie czy gra jest w bazie

        //jesli nie to zainicjalizowanie

        //wyslanie stanu planszy

        try {
            session.getBasicRemote().sendObject(game.getGameState());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(Session session, Message message){

        //znalezienie gry w bazie

        //wczytanie gry

        //wykonanie ruchu na grze

        //zapisanie stanu gry

        //wysłanie stanu nowej gry do obu graczy

        //zmienienie id aktualnego gracza
    }

    @OnClose
    public void onClose(){

    }

}

