package com.checkers.websocket;

import com.checkers.hibernate.util.ReaderDB;
import com.checkers.hibernate.util.SaverDB;
import com.checkers.model.game.Game;
import com.checkers.model.messages.Message;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

@ServerEndpoint(
        value = "/game/{userId}",
        decoders = UserMoveDecoder.class,
        encoders = BoardStateEncoder.class)
public class GameEndpoint {
    private ReaderDB readerDB = new ReaderDB();
    private SaverDB saverDB = new SaverDB();
    private Session session;
    private static HashMap<Integer, GameEndpoint> gameEndpoints = new HashMap<>();
    private Timer timer;

    @OnOpen
    public synchronized void onOpen(Session session, @PathParam("userId") Integer userId) {
        this.session = session;
        gameEndpoints.put(userId, this);

    }

    @OnMessage
    public void onMessage(Session session, Message message, @PathParam("userId") Integer user_Id) {
        Game game = null;
        int gameId = message.getGameId();
        int userId = message.getUserId();

        if ((game = readerDB.load(gameId)) == null) {
            game = new Game(gameId);
        }
        if (message.getMyColor() != null)
            game.setPlayerRole(message.getUserId(), message.getMyColor());

        game.readBoardState();
        if (message.getMessage()!=null)
            broadcastChat(game,message);

        System.out.println("\n" + message.toString());
        System.out.println("\n\n" + Arrays.deepToString(game.boardStateToSend(userId)));

        if (user_Id != game.getCurrentPlayerId()) {
            message.setBoard(game.boardStateToSend(userId));
            send(user_Id, message);
        }
        else if (userId == game.getCurrentPlayerId() && game.getBlackUser_id() != 0 && game.getWhiteUser_id() != 0) {
            try {
                message.setBoard(game.executeMessage(message.getMoveString(), userId));
                if (game.checkIfEnd())
                    message.winner(game.winner());
                message.setCurrentPlayer(game.getCurrentPlayerId() == game.getWhiteUser_id() ? 0 : 1);
                broadcast(game, message);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                saverDB.save(game);
            }
        }

    }

    private void broadcastChat(Game game, Message message) {
        try {
            gameEndpoints.get(game.getWhiteUser_id()).session.getBasicRemote().sendObject(message);
            gameEndpoints.get(game.getBlackUser_id()).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
    }

    private void send(Integer user_id, Message message) {
        try {
            gameEndpoints.get(user_id).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(Game game, Message message) {
        try {
            timer.schedule(new TimePassed(game), game.currentPlayerTimeLeft());
            message.setBoard(game.boardStateToSend(game.getBlackUser_id()));
            gameEndpoints.get(game.getBlackUser_id()).session.getBasicRemote().sendObject(message);
            message.setBoard(game.boardStateToSend(game.getWhiteUser_id()));
            gameEndpoints.get(game.getWhiteUser_id()).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }
    }

    private void broadcastWinner(Game game) {
        Message message = new Message(game.getId(), "winner-" + game.winner());
        try {
            gameEndpoints.get(game.getBlackUser_id()).session.getBasicRemote().sendObject(message);
            gameEndpoints.get(game.getWhiteUser_id()).session.getBasicRemote().sendObject(message);
        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }

    }

    class TimePassed extends TimerTask {
        private Game game;

        TimePassed(Game game) {
            this.game = game;
        }

        @Override
        public void run() {
            game.timeIsUpForCurrentPlayer();
            broadcastWinner(game);
        }
    }

}
