package com.checkers.model.game;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;


@Entity
@Table(name = "game")
public class Game {

    @Id
    @Column(name = "game_id")
    private int id;

    @Column(name = "whiteUser")
    private int whiteUser_id;

    @Column(name = "blackUser")
    private int blackUser_id;

    @Column(name = "boardState")
    private String boardState;

    @Column(name = "currentPlayerId")
    private int currentPlayerId;

    @Column(name = "whitePlayerTimeLeft")
    private int whitePlayerTimeLeft;

    @Column(name = "blackPlayerTimeLeft")
    private int blackPlayerTimeLeft;

    @Transient
    private Board board = new Board();
    @Transient
    private ArrayList<BlackPiece> blackTeamPieces = new ArrayList<>();
    @Transient
    private ArrayList<WhitePiece> whiteTeamPieces = new ArrayList<>();
    @Transient
    private Long timeStampStart;


    public Game(int id) {
        this.id = id;
        startGame();
    }


    public Game(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
        this.setCurrentPlayerId(whiteUser_id);
        startGame();
    }

    public Game() {
    }

    private void startGame() {
        initStartingPositions();
    }

    public String[][] executeMessage(String moveString, Integer userId) {
        ArrayList<Place> path = new ArrayList<>();
        for (String s : moveString.split("-"))
            path.add(new Place(s));
        Piece piece = board.getPlace(path.get(0)).getPieceOccupying();
        if (path.size() == 1) {//zwraca tablice mozliwych ruchow z ar/ab
            return boardStateToSend(piece.getPlace());
        } else {
            Move move = new Move(path.get(0), path.get(1));
            if (piece != null && ((userId == whiteUser_id && blackTeamPieces.contains(piece)) || (userId == blackUser_id && whiteTeamPieces.contains(piece))))
                throw new PlayerError("Trying to move not its own pieces");
            if (isSingleMovePossible(move, piece)) {
                System.out.println("\n*****\n" + move.toString());
                makeSingleMove(move);
                setBoardState(makeString4BoardState());
                if (move.isJump() && !canJump(board.getPlace(move.getDestination()).getPieceOccupying()))
                    switchPlayer();
            } else throw new PlayerError("Unexpected move");
        }
        return boardStateToSend(userId);
    }

    private boolean isSingleMovePossible(Move m, Piece piece) {

        int distance = Board.distance(m.getOrigin(), m.getDestination());
        if (canJump(piece)) {
            System.out.println("***********\n\nCan Jump");
            if (distance == 1) {
                System.out.println("***********\nDystans = 1 i może skoczyć\n***********");
                return false;
            }
            System.out.println("***********\n\nsearching if list of available jumps contains");
            return findListOfAvailableJumps(piece, m.getOrigin()).contains(m);
        } else {
            System.out.println("***********\n\nsearching if list of available moves contains");
            return findListOfAvailableMoves(piece).contains(m);
        }
    }

    private void makeSingleMove(Move move) {
        Piece piece = board.getPlace(move.getOrigin()).getPieceOccupying();
        Place placeToEmpty = board.placeBefore(move.getOrigin(), move.getDestination());
        board.emptyPlace(move.getOrigin());
        if (!placeToEmpty.equals(move.getOrigin())) {
            Piece pieceToRemove = board.getPlace(placeToEmpty).getPieceOccupying();
            System.out.println("\n****************\nRemoving " + pieceToRemove.toString());
            switch (pieceToRemove.getColor()) {
                case BLACK:
                    blackTeamPieces.remove(pieceToRemove);
                    board.emptyPlace(placeToEmpty);
                    break;
                case WHITE:
                    whiteTeamPieces.remove(pieceToRemove);
                    board.emptyPlace(placeToEmpty);
            }
        }
        board.getPlace(move.getDestination()).setPieceOccupying(piece);
    }

    private ArrayList<Move> findListOfAvailableMoves(Piece piece) {
        ArrayList<Move> validMoves = new ArrayList<>();
        Place placeOfOrigin = piece.getPlace();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = 0; i < 2; i++) {
                    Place placeOfInterest = new Place((char) (placeOfOrigin.getColumn() + piece.moveVector[i][0]), placeOfOrigin.getRow() + piece.moveVector[i][1]);
                    if (!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying() == null)
                        validMoves.add(new Move(placeOfOrigin, placeOfInterest));
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOfInterest = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        while (!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying() == null) {
                            validMoves.add(new Move(placeOfOrigin, placeOfInterest));
                            placeOfInterest = new Place((char) (placeOfInterest.getColumn() + i), placeOfInterest.getRow() + j);
                        }
                    }
                }
        }
        return validMoves;
    }

    private ArrayList<Move> findListOfAvailableJumps(Piece piece, Place placeOfOrigin) {
        ArrayList<Move> validJumps = new ArrayList<>();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeNextToPiece = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        Place placeBehindPlaceNextToPiece = new Place((char) (placeNextToPiece.getColumn() + i), placeNextToPiece.getRow() + j);
                        if (placeBehindPlaceNextToPiece.isOutOfBoard() || placeNextToPiece.isOutOfBoard()) continue;
                        Piece potentialPiece = board.getPlace(placeNextToPiece).getPieceOccupying();
                        if (potentialPiece != null) {
                            if (potentialPiece.getColor() == piece.getColor())
                                continue;
                            if (potentialPiece.getColor() != piece.getColor()
                                    && (board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null ||
                                    board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == piece)) {//case of possible jump
                                validJumps.add(new Move(placeOfOrigin, placeBehindPlaceNextToPiece));
                            }
                        }
                    }
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        Place placeBehindPlaceOnWay = new Place((char) (placeOnWay.getColumn() + i), placeOnWay.getRow() + j);
                        while (!placeBehindPlaceOnWay.isOutOfBoard()) {//checking in one direction
                            if (board.getPlace(placeOnWay).getPieceOccupying() == null) {
                                placeOnWay = placeBehindPlaceOnWay;
                                placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                            } else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() == piece.getColor())
                                break;
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() != piece.getColor()
                                    && (board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null ||
                                    board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == piece)) {//case of possible jump
                                validJumps.add(new Move(placeOfOrigin, placeBehindPlaceOnWay));
                            }
                        }
                    }
                }
        }
        return validJumps;
    }

    private boolean canMove(Piece piece) {
        Place placeOfOrigin = piece.getPlace();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = 0; i < 2; i++) {
                    Place placeOfInterest = new Place((char) (placeOfOrigin.getColumn() + piece.moveVector[i][0]), placeOfOrigin.getRow() + piece.moveVector[i][1]);
                    if (!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying() == null)
                        return true;
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOfInterest = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        if (!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying() == null)
                            return true;
                    }
                }
        }
        return false;
    }

    private boolean canJump(Piece piece) {
        Place placeOfOrigin = piece.getPlace();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeNextToPiece = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        Place placeBehindPlaceNextToPiece = new Place((char) (placeNextToPiece.getColumn() + i), placeNextToPiece.getRow() + j);
                        if (placeBehindPlaceNextToPiece.isOutOfBoard() || placeNextToPiece.isOutOfBoard()) continue;
                        Piece pieceOnPlaceNextToPiece = board.getPlace(placeNextToPiece).getPieceOccupying();
                        if (pieceOnPlaceNextToPiece != null) {
                            if (pieceOnPlaceNextToPiece.getColor() == piece.getColor())
                                continue;
                            if (pieceOnPlaceNextToPiece.getColor() != piece.getColor()
                                    && board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null)
                                return true;
                        }
                    }
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        Place placeBehindPlaceOnWay = new Place((char) (placeOnWay.getColumn() + i), placeOnWay.getRow() + j);
                        while (!placeBehindPlaceOnWay.isOutOfBoard()) {//checking in one direction
                            Piece pieceOnPlacedOnWay = board.getPlace(placeOnWay).getPieceOccupying();
                            if (pieceOnPlacedOnWay != null) {
                                if (pieceOnPlacedOnWay.getColor() == piece.getColor())
                                    break;
                                if (pieceOnPlacedOnWay.getColor() != piece.getColor()
                                        && board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null)
                                    return true;
                            }
                            placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                        }
                    }
                }
        }
        return false;
    }

    private boolean canTeamJump(PieceColor color) {
        switch (color) {
            case WHITE:
                for (Piece p : whiteTeamPieces)
                    if (canJump(p)) return true;
                break;
            case BLACK:
                for (Piece p : blackTeamPieces)
                    if (canJump(p)) return true;
        }
        return false;
    }

    public void readBoardState() {
        String[] string = boardState.split("-");
        for (String s : string)
            analyzeStringPositions(s);
    }

    private void analyzeStringPositions(String string) {
        Piece piece;
        switch (string.charAt(0)) {
            case 'w':
                piece = new WhitePiece();
                whiteTeamPieces.add((WhitePiece) piece);
                break;
            case 'b':
                piece = new BlackPiece();
                blackTeamPieces.add((BlackPiece) piece);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + string.charAt(0));
        }
        switch (string.charAt(1)) {
            case 'k':
                piece.setPieceType(PieceType.KING);
                break;
            case 'p':
                piece.setPieceType(PieceType.MEN);
                break;
        }
        board.getPlace(new Place(string.substring(2))).setPieceOccupying(piece);
    }

    private void initStartingPositions() {
        initBlackTeam();
        initWhiteTeam();
        checkStartingBoard();
        this.boardState = makeString4BoardState();
    }

    private void initBlackTeam() {
        blackTeamPieces = new ArrayList<>(12);
        Collections.reverse(board.getPlaces());
        if (board.getPlaces().get(0).getRow() != 8)
            throw new RuntimeException("Reverse during initialization of Black Team failed");
        for (int i = 0; i < 12; i++) {
            BlackPiece newBlack = new BlackPiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newBlack);
            blackTeamPieces.add(newBlack);
        }
        Collections.reverse(board.getPlaces());
        if (board.getPlaces().get(0).getRow() != 1)
            throw new RuntimeException("Second reverse during initialization of Black Team failed");
    }

    private void initWhiteTeam() {
        whiteTeamPieces = new ArrayList<>(12);
        for (int i = 0; i < 12; i++) {
            WhitePiece newWhite = new WhitePiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newWhite);
            whiteTeamPieces.add(newWhite);
        }
    }

    private void checkStartingBoard() {
        for (Place p : board.getPlaces()) {
            if (p.getPieceOccupying() != null)
                System.out.println(p.getPieceOccupying().toString());
            int rowOfPiece = p.getRow();
            if (rowOfPiece <= 3 && p.getPieceOccupying().getClass() != WhitePiece.class) {
                throw new RuntimeException("Wrong initialization, not White in row<3");
            } else if (rowOfPiece >= 4 && rowOfPiece <= 5 && p.getPieceOccupying() != null) {
                throw new RuntimeException("Wrong initialization, not null in 3<row<6");
            } else if (rowOfPiece >= 6 && p.getPieceOccupying().getClass() != BlackPiece.class) {
                throw new RuntimeException("Wrong initialization, not Black in row>5");
            }
        }

    }

    private String makeString4BoardState() {
        StringBuilder gameState = new StringBuilder();
        for (Piece p : whiteTeamPieces) {
            gameState.append(stringRepresentationOfPiece(p));
            gameState.append("-");
        }
        for (Piece p : blackTeamPieces) {
            gameState.append(stringRepresentationOfPiece(p));
            gameState.append("-");
        }
        setBoardState(gameState.toString());
        return gameState.toString();
    }

    private String stringRepresentationOfPiece(Piece piece) {
        return (piece.getColor() == PieceColor.WHITE ? "w" : "b") +
                (piece.getPieceType() == PieceType.KING ? "k" : "p") +
                piece.getPlace().toString();
    }

    private String[][] boardStateToSend(Place placeOfClick) {
        String[][] str = new String[8][8];
        Place place;
        Piece piece;
        for (int rowIterator = 0; rowIterator <= 7; rowIterator++) {
            for (char columnIterator = 0; columnIterator <= 7; columnIterator++) {
                str[rowIterator][columnIterator] = "";
                if ((place = board.getPlace(new Place((char) ('A' + columnIterator), rowIterator + 1))) != null) {
                    if ((piece = place.getPieceOccupying()) != null) {
                        if (placeOfClick == place) str[rowIterator][columnIterator] += "a";
                        str[rowIterator][columnIterator] += piece.stringToSend();
                    } else str[rowIterator][columnIterator] = "-";
                } else str[rowIterator][columnIterator] = "-";
            }
        }
        return str;
    }

    public String[][] boardStateToSend(int userId) {
        String[][] str = new String[8][8];
        Place place;
        Piece piece;
        PieceColor color = null;
        boolean currPlayer = false;
        boolean jumpsOnly = false;
        if (userId == currentPlayerId) {
            currPlayer = true;
            color = userId == getWhiteUser_id() ? PieceColor.WHITE : PieceColor.BLACK;
            jumpsOnly = canTeamJump(color);
        }
        for (int rowIterator = 0; rowIterator <= 7; rowIterator++) {
            for (char columnIterator = 0; columnIterator <= 7; columnIterator++) {
                str[rowIterator][columnIterator] = "";
                if ((place = board.getPlace(new Place((char) (columnIterator + 'A'), rowIterator + 1))) != null) {
                    if ((piece = place.getPieceOccupying()) != null) {
                        if (currPlayer && piece.getColor().equals(color)) {
                            if (jumpsOnly) {
                                if (canJump(piece)) {
                                    str[rowIterator][columnIterator] += "a";
                                    ArrayList<Move> jumps = findListOfAvailableJumps(piece, place);
                                    for (Move m : jumps) {
                                        int column = m.getDestination().getColumn() - 'A';
                                        int row = m.getDestination().getRow() - 1;
                                        if (str[column][row].equals(""))
                                            str[column][row] += "h";
                                    }
                                }
                            } else {
                                if (canMove(piece)) {
                                    str[rowIterator][columnIterator] += "a";
                                    ArrayList<Move> moves = findListOfAvailableMoves(piece);
                                    for (Move m : moves) {
                                        int column = m.getDestination().getColumn() - 'A';
                                        int row = m.getDestination().getRow() - 1;
                                        if (str[column][row].equals(""))
                                            str[column][row] += "h";
                                    }
                                }
                            }
                        }
                        str[rowIterator][columnIterator] += piece.stringToSend();
                    } else {
                        if (str[rowIterator][columnIterator].equals(""))
                            str[rowIterator][columnIterator] = "-";
                    }
                } else {
                    if (str[rowIterator][columnIterator].equals(""))
                        str[rowIterator][columnIterator] = "-";
                }
            }
        }
        return str;
    }

    public void switchPlayer() {

        this.setCurrentPlayerId(currentPlayerId == whiteUser_id ? blackUser_id : whiteUser_id);

    }

    public boolean checkIfEnd() {
        return whiteTeamPieces.size() == 0 || blackTeamPieces.size() == 0;
    }

    public int winner() {
        if (whiteTeamPieces.size() == 0 || whitePlayerTimeLeft == 0) return blackUser_id;
        if (blackTeamPieces.size() == 0 || blackPlayerTimeLeft == 0) return whiteUser_id;
        return 0;
    }

    public void setPlayerRole(int userId, Integer playerColor) {
        if (playerColor.compareTo(1) == 0) this.setBlackUser_id(userId);
        if (playerColor.compareTo(0) == 0) this.setWhiteUser_id(userId);
    }

    public String getBoardState() {
        return boardState;
    }

    private void setBoardState(String gameState) {
        this.boardState = gameState;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWhiteUser_id() {
        return whiteUser_id;
    }

    public void setWhiteUser_id(int whiteUser_id) {
        this.whiteUser_id = whiteUser_id;
        this.setCurrentPlayerId(whiteUser_id);
    }

    public int getBlackUser_id() {
        return blackUser_id;
    }

    public void setBlackUser_id(int blackUser_id) {
        this.blackUser_id = blackUser_id;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    private void setCurrentPlayerId(int currentPlayerId) {
        if (timeStampStart != null) {
            countDownTimeOfCurrentPlayer(System.currentTimeMillis() - timeStampStart);
        }
        this.currentPlayerId = currentPlayerId;
        timeStampStart = System.currentTimeMillis();
    }

    private void countDownTimeOfCurrentPlayer(long subTime) {
        if (currentPlayerId == blackUser_id) blackPlayerTimeLeft -= subTime;
        if (currentPlayerId == whiteUser_id) whitePlayerTimeLeft -= subTime;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getWhitePlayerTimeLeft() {
        return whitePlayerTimeLeft;
    }

    public void setWhitePlayerTimeLeft(int whitePlayerTimeLeft) {
        this.whitePlayerTimeLeft = whitePlayerTimeLeft;
    }

    public int getBlackPlayerTimeLeft() {
        return blackPlayerTimeLeft;
    }

    public void setBlackPlayerTimeLeft(int blackPlayerTimeLeft) {
        this.blackPlayerTimeLeft = blackPlayerTimeLeft;
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "id=" + id +
                ", whiteUser_id=" + whiteUser_id +
                ", blackUser_id=" + blackUser_id +
                '}';
    }

    public long currentPlayerTimeLeft() {
        return currentPlayerId == whiteUser_id ? whitePlayerTimeLeft : blackPlayerTimeLeft;
    }

    public void timeIsUpForCurrentPlayer() {
        if(currentPlayerId==blackUser_id)setBlackPlayerTimeLeft(0);
        if(currentPlayerId==whiteUser_id)setWhitePlayerTimeLeft(0);
    }


    private class PlayerError extends RuntimeException {
        PlayerError(String message) {
            super(message);
        }
    }
}
