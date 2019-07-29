package main.java.pieces;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.board.Move;
import main.java.board.Move.AttackMove;
import main.java.board.Move.MajorAttackMove;
import main.java.board.Move.MajorMove;
import main.java.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Bishop extends Piece {
    private final static int[] CANDIDATE_MOVE_VECTOR_COORDINATES = {-9, -7, 7, 9};

    public Bishop(Alliance pieceAlliance, int piecePos) {
        super(PieceType.BISHOP, piecePos, pieceAlliance, true);
    }

    public Bishop(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int candidateCoordinateOffset : CANDIDATE_MOVE_VECTOR_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePos;
            while (BoardUtils.isValidCoordinate((candidateDestinationCoordinate))) {
                if (isEighttColumnException(candidateDestinationCoordinate, candidateCoordinateOffset) ||
                        isFirstColumnException(candidateDestinationCoordinate, candidateCoordinateOffset)) {
                    break;
                }
                candidateDestinationCoordinate += candidateCoordinateOffset;
                if (BoardUtils.isValidCoordinate((candidateDestinationCoordinate))) {
                    final Tile candidateTile = board.getTile(candidateDestinationCoordinate);
                    if (!candidateTile.isTileOccupied()) {
                        legalMoves.add((new MajorMove(board, this, candidateDestinationCoordinate)));
                    } else {
                        final Piece pieceAtDestinaiton = candidateTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestinaiton.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new MajorAttackMove(board, this, candidateDestinationCoordinate, pieceAtDestinaiton));
                        }
                        break;
                    }

                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    private static boolean isFirstColumnException(final int currentPos, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPos] && (candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighttColumnException(final int currentPos, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPos] && (candidateOffset == -7 || candidateOffset == 9);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.toString();
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }
}
