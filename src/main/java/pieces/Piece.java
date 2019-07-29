package main.java.pieces;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.Move;

import java.util.Collection;

public abstract class Piece {

    protected  final PieceType pieceType;
    protected final int piecePos;
    protected final Alliance pieceAlliance;
    protected  final boolean isFirstMove;
    private  final int cachedHashCode;

    Piece(final PieceType pieceType, final int piecePos, final Alliance pieceAlliance, final boolean isFirstMove) {
        this.pieceAlliance = pieceAlliance;
        this.piecePos = piecePos;
        this.isFirstMove = isFirstMove;
        this.pieceType = pieceType;
        this.cachedHashCode = computeHashCode();
    }

    private int computeHashCode() {
        int result = pieceType.hashCode();
        result = 31 * result + pieceAlliance.hashCode();
        result = 31 * result + piecePos;
        result = 31* result + (isFirstMove ? 1 : 0);
        return result;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }
    public int getPiecePos() {
        return this.piecePos;
    }
    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }
    
    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }
    
    public abstract Piece movePiece (Move move);

    @Override
    public boolean equals (final Object other){
        if (this == other){
            return true;
        }
        if (!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return piecePos == otherPiece.getPiecePos() && pieceType == otherPiece.
        getPieceType() && pieceAlliance == otherPiece.getPieceAlliance() && isFirstMove == otherPiece.isFirstMove();
    }

    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }
    
    public enum PieceType {
        PAWN(1000,"P"){
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawns() {
                return true;
            }

            @Override
            public boolean isQueen() {
                return false;
            }
        },
        KNIGHT ( 3000, "N") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return true;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawns() {
                return false;
            }

            @Override
            public boolean isQueen() {
                return false;
            }
        },
        BISHOP(3000,"B") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return true;
            }

            @Override
            public boolean isPawns() {
                return false;
            }

            @Override
            public boolean isQueen() {
                return false;
            }
        },
        ROOK(5000,"R") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return true;
            }

            @Override
            public boolean isKnight() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawns() {
                return false;
            }

            @Override
            public boolean isQueen() {
                return false;
            }
        },
        QUEEN(9000,"Q") {
            @Override
            public boolean isKing() {
                return false;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawns() {
                return false;
            }

            @Override
            public boolean isQueen() {
                return true;
            }
        },
        KING(100000,"K") {
            @Override
            public boolean isKing() {
                return true;
            }
            @Override
            public boolean isRook() {
                return false;
            }

            @Override
            public boolean isKnight() {
                return false;
            }

            @Override
            public boolean isBishop() {
                return false;
            }

            @Override
            public boolean isPawns() {
                return false;
            }

            @Override
            public boolean isQueen() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(final int val,
                  final String pieceName) {
            this.pieceValue = val;
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();

        public abstract boolean isKnight();

        public abstract boolean isBishop() ;

        public abstract boolean isPawns();

        public abstract boolean isQueen();


        public int getPieceValue () {
            return this.pieceValue;
        }
    }
}
