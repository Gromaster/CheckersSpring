package com.checkers.websocket;

import com.checkers.hibernate.util.ReaderDB;
import com.checkers.hibernate.util.SaverDB;
import com.checkers.model.game.Game;
import com.checkers.model.messages.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

@ServerEndpoint(
        value = "/game/{userId}",
        decoders = UserMoveDecoder.class,
        encoders = BoardStateEncoder.class)
public class GameEndpoint {
    private ReaderDB readerDB = new ReaderDB();
    private SaverDB saverDB = new SaverDB();
    private Session session;
    private static HashMap<Integer, GameEndpoint> gameEndpoints = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        gameEndpoints.put(userId, this);

    }

    @OnMessage
    public void onMessage(Session session, Message message, @PathParam("userId") Integer user_Id) {
        Game game;
        int gameId = message.getGameId();
        int userId = message.getUserId();
        if ((game = readerDB.load(gameId)) == null)
            game = new Game(gameId, userId);
        else if (game.getBlackUser_id() == 0 && game.getWhiteUser_id() != userId) {
            game.setBlackUser_id(userId);
        } else if (game.getWhiteUser_id() == 0 && game.getBlackUser_id() != userId) {
            game.setWhiteUser_id(userId);

        }
        saverDB.save(game);
        System.out.println("\n" + message.toString());
        game.readBoardState();
        System.out.println("\n\n" + game.getBoard().toString());

        if (userId == game.getCurrentPlayerId() && game.getBlackUser_id() != 0 && game.getWhiteUser_id() != 0) {
            try {
                game.makeMove(message.getMoveString(), userId);
                if (game.checkIfEnd())
                    message.winner(game.winner());
                else message.setBoard(game.boardStateStringToSend());
                message.setCurrentPlayer(game.getCurrentPlayerId() == game.getWhiteUser_id() ? 0 : 1);
                broadcast(game, message);
                game.switchPlayer();
            } catch (Exception e) {
                System.out.println(game.getBoard().toString());
                e.printStackTrace();
                //broadcast(game,message.eraseMovement());
            }
        }
        saverDB.save(game);
    }


    private void broadcast(Game game, Message message) {
        try {
            gameEndpoints.get(game.getBlackUser_id()).session.getBasicRemote().sendObject(message);
            gameEndpoints.get(game.getWhiteUser_id()).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
    }

}
