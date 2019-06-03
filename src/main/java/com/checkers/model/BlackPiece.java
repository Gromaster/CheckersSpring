package com.checkers.model;

class BlackPiece extends Piece {


    BlackPiece(PieceType pieceType) {
        super(PieceColor.BLACK, pieceType);
        moveVector[0] = new int[]{1, -1};
        moveVector[1] = new int[]{-1, -1};
    }


    BlackPiece() {
        super(PieceColor.BLACK);
    }
}
