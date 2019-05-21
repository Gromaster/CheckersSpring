package com.checkers.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    @Id
    @Column(name = "game_id")
    private int id;

    @Column(name = "whiteUser")
    private int whiteUser_id;

    @Column(name = "blackUser")
    private int blackUser_id;

    @Column(name = "gameState")
    private String gameState;

    @Column(name = "currentPlayerId")
    private int currentPlayerId;


    @Transient
    private  Board board;
    @Transient
    private  ArrayList<BlackPiece> blackTeamPieces;
    @Transient
    private  ArrayList<WhitePiece> whiteTeamPieces;
    @Transient
    private  Results results;

    public Game(int id, int whiteUser_id, int blackUser_id) {
        this.id = id;
        this.whiteUser_id = whiteUser_id;
        this.blackUser_id = blackUser_id;
        this.currentPlayerId = whiteUser_id;
        startGame();
    }

    public Game() {
        startGame();
    }

    public void startGame() {
        board = new Board();
        initStartingPositions();
        results = new Results();
    }

    public void makeMove(String moveString){
        ArrayList<Place> path=new ArrayList<>();
        for(String s:moveString.split("-"))
            path.add(new Place(s));
        for(int i=0;i<(path.size()-1);i++) {
            Move move = new Move(path.get(i),path.get(i+1));
            makeMove(move);
        }
    }

    private void makeMove(Move move){
        Piece piece = board.getPlace(move.getOrigin()).getPieceOccupying();
        Place place2Empty = board.placeBefore(move.getOrigin(),move.getDestination());
        if(!place2Empty.equals(move.getOrigin()))
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

/*
    private void makeMove(Piece piece,Place destinationPlace){
        board.getPlace(piece.getPlace()).free();
        board.getPlace(destinationPlace).setPieceOccupying(piece);
        piece.setPlace(destinationPlace);
    }



    private void makeJump(Piece piece,ArrayList<Place> jumpTrace) {

        for (int i = 1; i < jumpTrace.size(); i++) {
            Place place2Empty = board.placeBefore(jumpTrace.get(i - 1), jumpTrace.get(i));
            Piece pieceToRemove = board.getPlace(place2Empty).getPieceOccupying();

            board.getPlace(piece.getPlace()).free();
            board.getPlace(jumpTrace.get(i)).setPieceOccupying(piece);
            piece.setPlace(jumpTrace.get(i));

            switch (pieceToRemove.getColor()) {
                case BLACK:
                    blackTeamPieces.remove(pieceToRemove);
                    board.setPieceOnPlaces(place2Empty, null);
                    break;
                case WHITE:
                    whiteTeamPieces.remove(pieceToRemove);
                    board.setPieceOnPlaces(place2Empty, null);
            }

        }
    }*/
/*

    private void WhiteTurn() {
        if(canTeamJump(whiteTeamPieces)){
            findTheMostEffectiveJump(whiteTeamPieces);
            if(checkIfRanOutOfPieces(blackTeamPieces))results.WhiteWon();
        }
        else if(canTeamMove(whiteTeamPieces)){//TODO
        }
        else results.BlackWon();
    }

    private void BlackTurn() {
        if(canTeamJump(blackTeamPieces)){
            findTheMostEffectiveJump(blackTeamPieces);
            if(checkIfRanOutOfPieces(whiteTeamPieces))results.BlackWon();
        }
        else if(canTeamMove(blackTeamPieces)){//TODO
        }
        else results.WhiteWon();
    }
*/

    private <T extends Piece> ArrayList<T> findAllMovablePieces(ArrayList<T> team) {
        ArrayList<T> movablePieces = new ArrayList<>();
        for(int i=0;i<team.size();i++){
            if(canMove(team.get(i)))movablePieces.add(team.get(i));
        }
        return movablePieces;
    }

    private <T extends Piece> ArrayList<Place> findAllMovesPossible(Piece piece){
        if(piece.getColor()==PieceColor.BLACK){
            piece=findPieceInTeam(piece, blackTeamPieces);
        }
        else {
            piece=findPieceInTeam(piece, whiteTeamPieces);
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
        blackTeamPieces =new ArrayList<BlackPiece>(12);
        Collections.reverse(board.getPlaces());
        if(board.getPlaces().get(0).getRow()!=8) throw new RuntimeException("Reverse during initialization of Black Team failed");
        for(int i=0;i<12;i++){
            BlackPiece newBlack=new BlackPiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newBlack);
            blackTeamPieces.add(newBlack);
        }
        Collections.reverse(board.getPlaces());
        if(board.getPlaces().get(0).getRow()!=1) throw new RuntimeException("Second reverse during initialization of Black Team failed");
    }

    private  void initWhiteTeam() {
        for(int i=0;i<12;i++){
            WhitePiece newWhite=new WhitePiece(PieceType.MEN);
            board.getPlaces().get(i).setPieceOccupying(newWhite);
            whiteTeamPieces.add(newWhite);
        }
    }

    public void setStringToParse(String stringToParse) {
        ArrayList<Integer> parsed = Stream.of(stringToParse.split("/")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));
        this.id = parsed.get(0);
        this.whiteUser_id = parsed.get(1);
        this.blackUser_id = parsed.get(2);
    }

    public String makeGameState(){
        StringBuilder gameState= new StringBuilder();
        for(Piece p:whiteTeamPieces){
            gameState.append(stringRepresentationOfPiece(p));
            gameState.append("-");
        }
        for(Piece p:blackTeamPieces){
            gameState.append(stringRepresentationOfPiece(p));
            gameState.append("-");
        }
        setGameState(gameState.toString());
        return gameState.toString();
    }

    private String stringRepresentationOfPiece(Piece piece){
        String result= "";
        result += (piece.getColor()==PieceColor.WHITE ? "w" : "b");
        result += (piece.getPieceType() == PieceType.KING ? "k" : "p");
        result += piece.getPlace().toString();
        return result;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
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
}
