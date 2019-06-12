package com.checkers.model.game;

class BlackPiece extends Piece {

    BlackPiece(PieceType pieceType) {
        super(PieceColor.BLACK, pieceType);
        moveVector[0] = new int[]{1, -1};
        moveVector[1] = new int[]{-1, -1};
    }

    BlackPiece() {
        super(PieceColor.BLACK);
        moveVector[0] = new int[]{1, -1};
        moveVector[1] = new int[]{-1, -1};
    }
}
