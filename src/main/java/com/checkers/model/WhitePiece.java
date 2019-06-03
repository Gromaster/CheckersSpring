package com.checkers.model;

public class WhitePiece extends Piece {
    public WhitePiece(PieceType pieceType) {
        super(PieceColor.WHITE, pieceType);
        moveVector[0] = new int[]{1, 1};
        moveVector[1] = new int[]{-1, 1};
    }

    public WhitePiece() {
        super(PieceColor.WHITE);
    }
}
