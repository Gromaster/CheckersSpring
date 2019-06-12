package com.checkers.model.game;

class WhitePiece extends Piece {

    WhitePiece(PieceType pieceType) {
        super(PieceColor.WHITE, pieceType);
        moveVector[0] = new int[]{1, 1};
        moveVector[1] = new int[]{-1, 1};
    }

    WhitePiece() {
        super(PieceColor.WHITE);
        moveVector[0] = new int[]{1, 1};
        moveVector[1] = new int[]{-1, 1};
    }
}
