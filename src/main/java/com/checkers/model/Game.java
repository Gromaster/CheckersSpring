package com.checkers.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private Board board=new Board();
    @Transient
    private ArrayList<BlackPiece> blackTeamPieces;
    @Transient
    private ArrayList<WhitePiece> whiteTeamPieces;

    public Game(int id, int userId) {
        this.id = id;
        Random r = new Random();
        if (r.nextBoolean())
            this.whiteUser_id = userId;
        else
            this.blackUser_id = userId;
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

    public void makeMove(String moveString) {
        ArrayList<Place> path = new ArrayList<>();
        for (String s : moveString.split("-"))
            path.add(new Place(s));
        ArrayList<Move> move = new ArrayList<>();
        for (int i = 0; i < (path.size() - 1); i++) {
            move.add(new Move(path.get(i), path.get(i + 1)));
        }
        System.out.println("\n\n*************************\n\n" + move.get(0).getOrigin() + " " + move.get(0).getDestination() + "\n***********************");
        System.out.println(isMovePossible(move));
        if (isMovePossible(move))
            makeMove(move);
        else throw new PlayerError("Unexpected move");
        setBoardState(makeString4BoardState());
    }

    private boolean isMovePossible(ArrayList<Move> moveList) {
        Piece piece = board.getPlace(moveList.get(0).getOrigin()).getPieceOccupying();
        if (piece == null) {
            System.out.println("******************\n\nPiece is null");
            return false;
        }
        for (Move m : moveList) {
            if (!isSingleMovePossible(m, piece)) return false;
        }
        return true;
    }

    private boolean isSingleMovePossible(Move m, Piece piece) {

        int distance = board.distance(m.getOrigin(), m.getDestination());
        if (canJump(piece)) {
            System.out.println("***********\n\nCan Jump");
            if (distance == 1) {
                System.out.println("********************************************\nDystans = 1 i może skoczyć\n********************************************************");
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
        Place place2Empty = board.placeBefore(move.getOrigin(), move.getDestination());
        if (!place2Empty.equals(move.getOrigin()))
            board.getPlace(move.getOrigin()).free();
        else {
            Piece piece2Remove = board.getPlace(place2Empty).getPieceOccupying();
            switch (piece2Remove.getColor()) {
                case BLACK:
                    blackTeamPieces.remove(piece2Remove);
                    board.setPieceOnPlaces(place2Empty, null);
                    break;
                case WHITE:
                    whiteTeamPieces.remove(piece2Remove);
                    board.setPieceOnPlaces(place2Empty, null);
            }

        }
        board.getPlace(move.getDestination()).setPieceOccupying(piece);
        piece.setPlace(move.getDestination());
    }

    private <T extends Piece> ArrayList<T> findAllMovablePieces(ArrayList<T> team) {
        ArrayList<T> movablePieces = new ArrayList<>();
        for (int i = 0; i < team.size(); i++) {
            if (canMove(team.get(i))) movablePieces.add(team.get(i));
        }
        return movablePieces;
    }

    private <T extends Piece> ArrayList<Move> findAllMovesPossible(Piece piece) {
        if (piece.getColor() == PieceColor.BLACK) {
            piece = findPieceInTeam(piece, blackTeamPieces);
        } else {
            piece = findPieceInTeam(piece, whiteTeamPieces);
        }
        return findListOfAvailableMoves(piece);
    }

    private <T extends Piece> Piece findPieceInTeam(Piece piece, ArrayList<T> team) {
        for (int i = 0; i < team.size(); i++) {
            if (piece.getPlace().equals(team.get(i).getPlace())) return team.get(i);
        }
        throw new RuntimeException("Failed to find searching piece in team");
    }

    private <T extends Piece> boolean checkIfRanOutOfPieces(ArrayList<T> team) {
        return team.size() == 0;
    }

    private <T extends Piece> boolean canTeamMove(ArrayList<T> team) {
        for (Piece piece : team) {
            if (canMove(piece)) return true;
        }
        return false;
    }

    /**
     * Checking if any of team member can make a jump
     *
     * @param team pieces belonging to team organized in ArrayList
     * @param <T>  class of pieces(white or black)
     * @return true if any member can jump, otherwise false
     */
    private <T extends Piece> boolean canTeamJump(ArrayList<T> team) {
        for (Piece piece : team) {
            if (canJump(piece)) return true;
        }
        return false;
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
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() == piece.getColor())
                            continue;
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() != piece.getColor()
                                && (board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null ||
                                board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == piece)) {//case of possible jump
                            validJumps.add(new Move(placeOfOrigin, placeBehindPlaceNextToPiece));
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
                    Place placeNextToPiece = new Place((char) (placeOfOrigin.getColumn() + piece.moveVector[i][0]), placeOfOrigin.getRow() + piece.moveVector[i][1]);
                    if (!placeNextToPiece.isOutOfBoard() && board.getPlace(placeNextToPiece).getPieceOccupying() == null)
                        return true;
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (placeOfOrigin.getColumn() + i), placeOfOrigin.getRow() + j);
                        if (!placeOnWay.isOutOfBoard() && board.getPlace(placeOnWay).getPieceOccupying() == null)
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
                        Piece pieceOnPlaceNextToPiece =board.getPlace(placeNextToPiece).getPieceOccupying();
                        if(pieceOnPlaceNextToPiece!=null){
                            if (pieceOnPlaceNextToPiece.getColor() == piece.getColor())
                                continue;
                            if (pieceOnPlaceNextToPiece.getColor() != piece.getColor()
                                    && board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null) return true;
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
                                        && board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null) return true;
                            }
                            placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                        }
                    }
                }
        }
        return false;
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

    public void setStringToParse(String stringToParse) {
        ArrayList<Integer> parsed = Stream.of(stringToParse.split("/")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
        this.id = parsed.get(0);
        this.whiteUser_id = parsed.get(1);
        this.blackUser_id = parsed.get(2);
    }

    public String makeString4BoardState() {
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

    public void setBoardState(String gameState) {
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

    public void readBoardState() {
        String[] string = boardState.split("-");
        for(String s:string)
            analyzeStringPositions(s);
    }

    private void analyzeStringPositions(String string) {
        Piece piece ;
        switch (string.charAt(0)){
            case 'w':
                piece=new WhitePiece();
                break;
            case 'b':
                piece=new BlackPiece();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + string.charAt(0));
        }
        switch (string.charAt(1)){
            case 'k':
                piece.setPieceType(PieceType.KING);
                break;
            case 'p':
                piece.setPieceType(PieceType.MEN);
                break;
        }
        board.getPlace(new Place(string.substring(2))).setPieceOccupying(piece);
    }

    private class PlayerError extends RuntimeException {
        PlayerError(String message) {
            super(message);

        }

    }
}
