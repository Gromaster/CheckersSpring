package com.checkers.model;

public class BlackPiece extends Piece {


    public BlackPiece(PieceType pieceType) {
        super(PieceColor.BLACK, pieceType);
        moveVector[0] = new int[]{1, -1};
        moveVector[1] = new int[]{-1, -1};
    }


    public BlackPiece() {
        super(PieceColor.BLACK);
    }
}
