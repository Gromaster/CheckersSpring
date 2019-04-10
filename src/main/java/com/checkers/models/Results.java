package com.checkers.models;

public class Results {
    private PieceColor colorOfWinningTeam=null;
    private boolean gameOver=false;
    private int turnWithoutJumpingCounter=0;

    public void WhiteWon() {

        colorOfWinningTeam=PieceColor.WHITE;
        gameOver=true;
    }

    public void BlackWon() {

        colorOfWinningTeam=PieceColor.BLACK;
        gameOver=true;
    }

    public void Draw() {

        gameOver=true;
    }

    public PieceColor getColorOfWinningTeam() {
        return colorOfWinningTeam;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void increaseCounter(){
        turnWithoutJumpingCounter++;
        if(turnWithoutJumpingCounter>=15)Draw();
    }
}
