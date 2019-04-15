package com.checkers.models;

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

    private void makeMove(Piece piece,Place destinationPlace){
        board.getPlace(piece.getPlace()).free();
        board.getPlace(destinationPlace).setPieceOccupying(piece);
        piece.setPlace(destinationPlace);
    }

    private void makeJump(Piece piece,ArrayList<Place> jumpTrace) {
        for (int i = 1; i < jumpTrace.size(); i++) {
            Place placeOfDestination = board.placeBefore(jumpTrace.get(i - 1), jumpTrace.get(i));
            Piece pieceToRemove = board.getPlace(placeOfDestination).getPieceOccupying();

            board.getPlace(piece.getPlace()).free();
            board.getPlace(jumpTrace.get(i)).setPieceOccupying(piece);
            piece.setPlace(jumpTrace.get(i));

            switch (pieceToRemove.getColor()) {
                case BLACK:
                    blackTeam.remove(pieceToRemove);
                    board.setPieceOnPlaces(placeOfDestination, null);
                    break;
                case WHITE:
                    whiteTeam.remove(pieceToRemove);
                    board.setPieceOnPlaces(placeOfDestination, null);
            }

        }
    }

    private void WhiteTurn() {
        if(canTeamJump(whiteTeam)){
            findTheMostEffectiveJump(whiteTeam);
            if(checkIfRanOutOfPieces(blackTeam))results.WhiteWon();
        }
        else if(canTeamMove(whiteTeam)){//TODO
        }
        else results.BlackWon();
    }

    private void BlackTurn() {
        if(canTeamJump(blackTeam)){
            findTheMostEffectiveJump(blackTeam);
            if(checkIfRanOutOfPieces(whiteTeam))results.BlackWon();
        }
        else if(canTeamMove(blackTeam)){//TODO
        }
        else results.WhiteWon();
    }

    private <T extends Piece> ArrayList<T> findAllMovablePieces(ArrayList<T> team) {
        ArrayList<T> movablePieces = new ArrayList<>();
        for(int i=0;i<team.size();i++){
            if(canMove(team.get(i)))movablePieces.add(team.get(i));
        }
        return movablePieces;
    }

    private <T extends Piece> ArrayList<Place> findAllMovesPossible(Piece piece){
        if(piece.getColor()==PieceColor.BLACK){
            piece=findPieceInTeam(piece,blackTeam);
        }
        else {
            piece=findPieceInTeam(piece,whiteTeam);
        }
        return findListOfAvailableMoves(piece);
    }

    private <T extends Piece> Piece findPieceInTeam(Piece piece, ArrayList<T> team) {
        for(int i=0;i<team.size();i++){
            if(piece.getPlace().equals(team.get(i).getPlace())) return team.get(i);
        }
        throw new RuntimeException("Failed to find searching piece in team");
    }

    private <T extends Piece> boolean checkIfRanOutOfPieces(ArrayList<T> team) {
        return team.size() == 0;
    }

    private <T extends Piece> boolean canTeamMove(ArrayList<T> team) {
        for(Piece piece:team){
            if(canMove(piece))return true;
        }
        return false;
    }

    /**
     * Checking if any of team member can make a jump
     *
     * @param team  pieces belonging to team organized in ArrayList
     * @param <T>
     * @return      true if any member can jump, otherwise false
     */
    private <T extends Piece> boolean canTeamJump(ArrayList<T> team) {
        for(Piece piece:team){
            if(canJump(piece))return true;
        }
        return false;
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

    private <T extends Piece> ArrayList<Place> findTheMostEffectiveJump(ArrayList<T> team) {
        ArrayList<Place> mostEffectiveJump=new ArrayList<>();
        JumpTree jumpTree;
        for(int i=0;i<team.size();i++){
            jumpTree=new JumpTree(new NodeOfJumpTree(team.get(i).getPlace()));
            jumpTree.root=findListOfAvailableJumps(team.get(i),team.get(i).getPlace());

            if(mostEffectiveJump.size()<jumpTree.getMaxRoute(jumpTree.root).size())
                mostEffectiveJump=jumpTree.getMaxRoute(jumpTree.root);
            //compare and find the longest jump of the whole team
        }

        return mostEffectiveJump;
    }

    private NodeOfJumpTree findListOfAvailableJumps(Piece piece, Place placeOfPiece) {
        ArrayList<Place> validPlaces=new ArrayList<>();
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
                        if ( board.getPlace(placeNextToPiece).getPieceOccupying().getColor() != piece.getColor()
                                && ( board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying() == null ||
                                board.getPlace(placeBehindPlaceNextToPiece).getPieceOccupying()==piece ) ) {//case of possible jump
                            currNode.setNextChild(findListOfAvailableJumps(piece,placeBehindPlaceNextToPiece));
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
                            if (board.getPlace(placeOnWay).getPieceOccupying() == null) {
                                placeOnWay = placeBehindPlaceOnWay;
                                placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j); }
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() == piece.getColor())
                                break;
                            else if (board.getPlace(placeOnWay).getPieceOccupying().getColor() != piece.getColor()
                                    && (board.getPlace(placeBehindPlaceOnWay).getPieceOccupying() == null ||
                                    board.getPlace(placeBehindPlaceOnWay).getPieceOccupying()==piece )) {//case of possible jump
                                currNode.setNextChild(findListOfAvailableJumps(piece,placeBehindPlaceOnWay));
                                //validPlaces.add(placeBehindPlaceOnWay);
                            }
                            /*placeOnWay = placeBehindPlaceOnWay;
                            placeBehindPlaceOnWay = new Place((char) (placeBehindPlaceOnWay.getColumn() + i), placeBehindPlaceOnWay.getRow() + j);*/
                        }
                    }
                }
        }
        return new NodeOfJumpTree(placeOfPiece);
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
