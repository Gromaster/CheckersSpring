package com.checkers.models;

import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private static Board board;
    private static ArrayList<BlackPiece> blackTeam;
    private static ArrayList<WhitePiece> whiteTeam;
    private static Results results;

    public static void main(String[] args) {
        board = new Board();
        initStartingPositions();
        results = new Results();
        while (!results.isGameOver()) {
            WhiteTurn();
            if(!results.isGameOver())break;
            BlackTurn();
        }

    }

    private static void WhiteTurn() {
        if(canTeamJump(whiteTeam)){
            findTheMostEffectiveJump(whiteTeam);
        }
        else if(canTeamMove(whiteTeam)){
            findAllMovesPossible(whiteTeam);
        }
        else results.BlackWon();
    }


    private static void BlackTurn() {
        if(canTeamJump(blackTeam)){
            findTheMostEffectiveJump(blackTeam);
        }
        else if(canTeamMove(blackTeam)){
            findAllMovesPossible(blackTeam);
        }
        else results.WhiteWon();
    }

    private static <T extends Piece> void findTheMostEffectiveJump(ArrayList<T> team) {//TODO
    }

    private static <T extends Piece> void findAllMovesPossible(ArrayList<T> team) {//TODO

    }

    private static <T extends Piece> boolean canTeamMove(ArrayList<T> team) {
        for(Piece piece:team){
            if(canMove(piece))return true;
        }
        return false;
    }

    private static <T extends Piece> boolean canTeamJump(ArrayList<T> team) {
        for(Piece piece:team){
            if(canJump(piece))return true;
        }
        return false;
    }

    static ArrayList<Place> validMoves(Piece piece){
        if(canJump(piece)){
            return findListOfAvailableJumps(piece, piece.getPlace());
        }
        else if (canMove(piece)){
            return findListOfAvailableMoves(piece);
        }
        else return null;
    }

    private static ArrayList<Place> findListOfAvailableMoves(Piece piece) {//TODO
        ArrayList<Place> validPlaces=new ArrayList<Place>();

        return validPlaces;
    }





    private static ArrayList<Place> findListOfAvailableJumps(Piece piece, Place placeOfPiece) {
        ArrayList<Place> validPlaces=new ArrayList<Place>();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeNextToPiece = new Place((char) (placeOfPiece.getColumn() + i), placeOfPiece.getRow() + j);
                        Place placeBehindPlaceNextToPiece = new Place((char) (placeNextToPiece.getColumn() + i), placeNextToPiece.getRow() + j);
                        if (placeBehindPlaceNextToPiece.isOutOfBoard() || placeNextToPiece.isOutOfBoard()) continue;
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() == piece.getColor())
                            continue;
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() != piece.getColor()
                                && board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null) {
                            validPlaces.add(placeBehindPlaceNextToPiece);
                        }
                    }
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (piece.getPlace().getColumn() + i), piece.getPlace().getRow() + j);
                        Place placeBehindPlaceOnWay = new Place((char) (placeOnWay.getColumn() + i), placeOnWay.getRow() + j);
                        while (!placeBehindPlaceOnWay.isOutOfBoard()) {//checking in one direction
                            if (board.getPlace(placeOnWay).getPieceOccupying() == null) { }
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() == piece.getColor())
                                break;
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() != piece.getColor()
                                    && board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null) {
                                validPlaces.add(placeBehindPlaceOnWay);
                            }
                            placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                        }
                    }
                }
        }
        return validPlaces;
    }

    private static boolean canMove(Piece piece) {
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = 0; i < 2; i++) {
                    Place placeNextToPiece = new Place((char) (piece.getPlace().getColumn() + piece.moveVector[i][0]), piece.getPlace().getRow() + piece.moveVector[i][1]);
                    if(!placeNextToPiece.isOutOfBoard() && board.getPlace(placeNextToPiece).getPieceOccupying()==null) return true;
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (piece.getPlace().getColumn() + i), piece.getPlace().getRow() + j);
                        if(!placeOnWay.isOutOfBoard() && board.getPlace(placeOnWay).getPieceOccupying() == null)return true;
                    }
                }
        }
        return false;
    }

    private static boolean canJump(Piece piece) {
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeNextToPiece = new Place((char) (piece.getPlace().getColumn() + i), piece.getPlace().getRow() + j);
                        Place placeBehindPlaceNextToPiece = new Place((char) (placeNextToPiece.getColumn() + i), placeNextToPiece.getRow() + j);
                        if (placeBehindPlaceNextToPiece.isOutOfBoard() || placeNextToPiece.isOutOfBoard()) continue;
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() == piece.getColor())
                            continue;
                        if (board.getPlace(placeNextToPiece).getPieceOccupying().getColor() != piece.getColor()
                                && board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null) return true;
                    }
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOnWay = new Place((char) (piece.getPlace().getColumn() + i), piece.getPlace().getRow() + j);
                        Place placeBehindPlaceOnWay = new Place((char) (placeOnWay.getColumn() + i), placeOnWay.getRow() + j);
                        while (!placeBehindPlaceOnWay.isOutOfBoard()) {//checking in one direction
                            if(board.getPlace(placeOnWay).getPieceOccupying()==null){}
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() == piece.getColor()) break;
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() != piece.getColor()
                                    && board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null) return true;
                            placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                        }
                    }
                }
        }
        return false;
    }

    static void initStartingPositions(){
        initBlackTeam();
        initWhiteTeam();
        checkStartingBoard();
    }

    private static void checkStartingBoard() {
        for(Place p:board.getPlaces()){
            if(p.getRow()<=3 && p.getPieceOccupying().getClass()!=WhitePiece.class) throw new RuntimeException("Wrong initialization, not White in row<3");
            else if (p.getRow()<=5 && p.getPieceOccupying()!=null) throw new RuntimeException("Wrong initialization, not null in 3<row<6");
            else if(p.getRow()<=8 && p.getPieceOccupying().getClass()!=BlackPiece.class) throw new RuntimeException("Wrong initialization, not Black in row>5");
        }

    }

    private static void initBlackTeam() {
        blackTeam=new ArrayList<BlackPiece>(12);
        Collections.reverse(board.getPlaces());
        if(board.getPlaces().get(0).getRow()!=8) throw new RuntimeException("Reverse during initialization of Black Team failed");
        for(int i=0;i<12;i++){
            BlackPiece newBlack=new BlackPiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newBlack);
            blackTeam.add(newBlack);
        }
        Collections.reverse(board.getPlaces());
        if(board.getPlaces().get(0).getRow()!=1) throw new RuntimeException("Second reverse during initialization of Black Team failed");
    }

    private static void initWhiteTeam() {
        for(int i=0;i<12;i++){
            WhitePiece newWhite=new WhitePiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newWhite);
            whiteTeam.add(newWhite);
        }
    }
    
    
}
