package main.java.pieces;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.Move;
import main.java.board.Move.AttackMove;
import main.java.board.Move.MajorMove;
import main.java.board.Tile;
import main.java.board.BoardUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Knight extends Piece {

    private final static int[] legalMove = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight( final Alliance pieceAlliance, final int piecePos) {
        super(PieceType.KNIGHT,piecePos, pieceAlliance, true);
    }

    public Knight (final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentMove : legalMove) {
            final int candidateCoordinate = this.piecePos + currentMove;
            if (BoardUtils.isValidCoordinate(candidateCoordinate)) {
                if (isFirstColumnException(this.piecePos, currentMove) || isSecondColumnExclusion(this.piecePos, currentMove) ||
                        isSeventhColumnExclusion(this.piecePos, currentMove) ||
                        isEighthColumnExclusion(this.piecePos, currentMove)) {
                    continue;
                }
                final Tile candidateTile = board.getTile(candidateCoordinate);
                if (!candidateTile.isTileOccupied()) {
                    legalMoves.add((new MajorMove(board, this, candidateCoordinate)));
                } else {
                    final Piece pieceAtDestinaiton = candidateTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestinaiton.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateCoordinate, pieceAtDestinaiton));
                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    private static boolean isFirstColumnException(final int currentPos, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && ((candidateOffset == -17) || (candidateOffset == -10) || (candidateOffset == 6) || (candidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((candidateOffset == -10) || (candidateOffset == 6));
    }

    private static boolean isSeventhColumnExclusion(final int currentPosition,
                                                    final int candidateOffset) {
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((candidateOffset == -6) || (candidateOffset == 10));
    }

    private static boolean isEighthColumnExclusion(final int currentPosition,
                                                   final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateOffset == -15) || (candidateOffset == -6) ||
                (candidateOffset == 10) || (candidateOffset == 17));
    }
    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }
    @Override
    public Knight movePiece (Move move){
        return new Knight(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate() );
    }
}
