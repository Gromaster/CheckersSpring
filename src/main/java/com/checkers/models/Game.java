package com.checkers.models;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.Collections;

public class Game {
    private  Board board;
    private  ArrayList<BlackPiece> blackTeam;
    private  ArrayList<WhitePiece> whiteTeam;
    private  Results results;
    
    
    public void startGame() {
        board = new Board();
        initStartingPositions();
        results = new Results();
        while (!results.isGameOver()) {
            WhiteTurn();
            if (!results.isGameOver()) break;
            BlackTurn();
        }
    }

    private void WhiteTurn() {
        if(canTeamJump(whiteTeam)){
            findTheMostEffectiveJump(whiteTeam);
            if(checkIfRanOutOfPieces(blackTeam))results.WhiteWon();
        }
        else if(canTeamMove(whiteTeam)){
            findAllMovesPossible(whiteTeam);
        }
        else results.BlackWon();
    }



    private void BlackTurn() {
        if(canTeamJump(blackTeam)){
            findTheMostEffectiveJump(blackTeam);
            if(checkIfRanOutOfPieces(whiteTeam))results.BlackWon();
        }
        else if(canTeamMove(blackTeam)){
            findAllMovesPossible(blackTeam);
        }
        else results.WhiteWon();
    }





    private <T extends Piece> boolean checkIfRanOutOfPieces(ArrayList<T> team) {
        if(team.size()==0)return true;
        return false;
    }
        
    private <T extends Piece> boolean canTeamMove(ArrayList<T> team) {
        for(Piece piece:team){
            if(canMove(piece))return true;
        }
        return false;
    }

    private <T extends Piece> boolean canTeamJump(ArrayList<T> team) {
        for(Piece piece:team){
            if(canJump(piece))return true;
        }
        return false;
    }

    ArrayList<Place> validMoves(Piece piece){
        if(canJump(piece)){
            return findListOfAvailableJumps(piece, piece.getPlace());
        }
        else if (canMove(piece)){
            return findListOfAvailableMoves(piece);
        }
        else return null;
    }

    private ArrayList<Place> findListOfAvailableMoves(Piece piece) {
        ArrayList<Place> validPlaces=new ArrayList<Place>();
        switch (piece.getPieceType()) {
            case MEN:
                for (int i = 0; i < 2; i++) {
                    Place placeOfInterest = new Place((char) (piece.getPlace().getColumn() + piece.moveVector[i][0]), piece.getPlace().getRow() + piece.moveVector[i][1]);
                    if(!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying()==null) validPlaces.add(placeOfInterest);
                }
                break;
            case KING:
                for (int i = -1; i < 2; i += 2) {
                    for (int j = -1; j < 2; j += 2) {
                        Place placeOfInterest = new Place((char) (piece.getPlace().getColumn() + i), piece.getPlace().getRow() + j);
                        while(!placeOfInterest.isOutOfBoard() && board.getPlace(placeOfInterest).getPieceOccupying() == null){
                            validPlaces.add(placeOfInterest);
                            placeOfInterest = new Place((char) (placeOfInterest.getColumn()+i),placeOfInterest.getRow()+j);
                        }
                    }
                }
        }
        return validPlaces;
    }



    private <T extends Piece>void findTheMostEffectiveJump(ArrayList<T> team) {//TODO
        JumpTree jumpTree;
        for(int i=0;i<team.size();i++){
            jumpTree=new JumpTree(new NodeOfJumpTree(team.get(i).getPlace()));
            jumpTree.root=findListOfAvailableJumps();
            //compare and find the longest jump of the whole team
        }

    }

    private NodeOfJumpTree findListOfAvailableJumps(Piece piece, Place placeOfPiece) {
        ArrayList<Place> validPlaces=new ArrayList<Place>();
        NodeOfJumpTree currNode=new NodeOfJumpTree(placeOfPiece);
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
                            //BUILD THE TREE AND IMPLEMENT RECURENCY
                            //validPlaces.add(placeBehindPlaceNextToPiece);
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
                            //noinspection StatementWithEmptyBody
                            if (board.getPlace(placeOnWay).getPieceOccupying() == null) { }
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() == piece.getColor())
                                break;
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() != piece.getColor()
                                    && board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null) {
                                validPlaces.add(placeBehindPlaceOnWay);
                            }
                            //BUILD THE TREE AND IMPLEMENT RECURENCY
                            placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);
                        }
                    }
                }
        }
        return validPlaces;
    }

    private  boolean canMove(Piece piece) {
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

    private  boolean canJump(Piece piece) {
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
                            //noinspection StatementWithEmptyBody
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

    private  void initStartingPositions(){
        initBlackTeam();
        initWhiteTeam();
        checkStartingBoard();
    }

    private  void checkStartingBoard() {
        for(Place p:board.getPlaces()){
            if(p.getRow()<=3 && p.getPieceOccupying().getClass()!=WhitePiece.class) throw new RuntimeException("Wrong initialization, not White in row<3");
            else if (p.getRow()<=5 && p.getPieceOccupying()!=null) throw new RuntimeException("Wrong initialization, not null in 3<row<6");
            else if(p.getRow()<=8 && p.getPieceOccupying().getClass()!=BlackPiece.class) throw new RuntimeException("Wrong initialization, not Black in row>5");
        }

    }

    private  void initBlackTeam() {
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

    private  void initWhiteTeam() {
        for(int i=0;i<12;i++){
            WhitePiece newWhite=new WhitePiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newWhite);
            whiteTeam.add(newWhite);
        }
    }
    
    
}
