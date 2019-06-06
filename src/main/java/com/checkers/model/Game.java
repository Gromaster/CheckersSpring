package com.checkers.model;

import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


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


    @Transient
    private Board board = new Board();
    @Transient
    private ArrayList<BlackPiece> blackTeamPieces = new ArrayList<>();
    @Transient
    private ArrayList<WhitePiece> whiteTeamPieces = new ArrayList<>();

    public Game(int id, int userId) {
        this.id = id;
        Random r = new Random();
        if (r.nextBoolean())
            setWhiteUser_id(userId);
        else
            setBlackUser_id(userId);
        startGame();
    }


    public Game(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
        this.currentPlayerId = whiteUser_id;
        startGame();
    }

    public Game() {
    }

    private void startGame() {
        initStartingPositions();
    }

    public void makeMove(String moveString, Integer userId) {
        ArrayList<Place> path = new ArrayList<>();
        for (String s : moveString.split("-"))
            path.add(new Place(s));
        ArrayList<Move> move = new ArrayList<>();
        for (int i = 0; i < (path.size() - 1); i++) {
            move.add(new Move(path.get(i), path.get(i + 1)));
        }
        Piece piece = board.getPlace(move.get(0).getOrigin()).getPieceOccupying();
        if (piece != null && ((userId == whiteUser_id && blackTeamPieces.contains(piece)) || (userId == blackUser_id && whiteTeamPieces.contains(piece))))
            throw new PlayerError("Trying to move not its own pieces");
        if (isMovePossible(move)) {
            System.out.println("\n*****\n" + move.toString());
            makeMove(move);
        } else throw new PlayerError("Unexpected move");
        setBoardState(makeString4BoardState());
    }

    private boolean isMovePossible(ArrayList<Move> moveList) {
        Piece piece = board.getPlace(moveList.get(0).getOrigin()).getPieceOccupying();
        if (piece == null) {
            System.out.println("***********\n\nPiece is null");
            return false;
        }
        if(!moveList.get(0).isJump() && canTeamJump(piece))return false;
        for (Move m : moveList) {
            if (!isSingleMovePossible(m, piece)) return false;
        }
        return true;
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

    private void makeMove(ArrayList<Move> moveList) {
        for (Move m : moveList) {
            makeSingleMove(m);
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

    private boolean canTeamJump(Piece piece) {
        switch (piece.getColor()){
            case WHITE:
                for(Piece p:whiteTeamPieces)
                    if(canJump(p))return true;
                break;
            case BLACK:
                for (Piece p:blackTeamPieces)
                    if(canJump(p))return true;
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
        String builder = (piece.getColor() == PieceColor.WHITE ? "w" : "b") +
                (piece.getPieceType() == PieceType.KING ? "k" : "p") +
                piece.getPlace().toString();
        return builder;
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
        this.currentPlayerId = whiteUser_id;
        this.whiteUser_id = whiteUser_id;
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

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    @Override
    public String toString() {
        return "GameEntity{" +
                "id=" + id +
                ", whiteUser_id=" + whiteUser_id +
                ", blackUser_id=" + blackUser_id +
                '}';
    }

    public void switchPlayer() {
        currentPlayerId = (currentPlayerId == whiteUser_id ? blackUser_id : whiteUser_id);
    }

    public boolean checkIfEnd() {
        return whiteTeamPieces.size() == 0 || blackTeamPieces.size() == 0;
    }

    public int winner() {
        if (whiteTeamPieces.size() == 0) return blackUser_id;
        if (blackTeamPieces.size() == 0) return whiteUser_id;
        return 0;
    }

    private class PlayerError extends RuntimeException {
        PlayerError(String message) {
            super(message);
        }
    }
}
