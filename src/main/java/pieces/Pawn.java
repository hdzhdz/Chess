package main.java.pieces;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.board.Move;
import main.java.board.Move.MajorMove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {
    private final static int[] CANDIDATE_MOVE_COORDINATE = {7, 8, 9, 16};

    public Pawn( final Alliance pieceAlliance,final int piecePos) {
        super(PieceType.PAWN, piecePos, pieceAlliance, true);
    }

    public Pawn (final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove){
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }
    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            int candidateDestinationCoordinate =
                    this.piecePos + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            if (!BoardUtils.isValidCoordinate((candidateDestinationCoordinate))) {
                continue;
            }
            if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this,
                        candidateDestinationCoordinate)));
                } else {
                    legalMoves.add(new Move.PawnMove(board, this, candidateDestinationCoordinate));
                }
            } else if (currentCandidateOffset == 16 && this.isFirstMove() &&
                    ((BoardUtils.SECOND_ROW[this.piecePos] && this.getPieceAlliance().isBlack()) ||
                    (BoardUtils.SEVENTH_ROW[this.piecePos] && this.getPieceAlliance().isWhite()))) {
                final int behindCandidateDestinationCoordinate = this.piecePos + this.pieceAlliance.getDirection() * 8;
                if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied() &&
                    !board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    legalMoves.add(new Move.PawnJump(board, this, candidateDestinationCoordinate));

                }

            } else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceAlliance.isBlack()))) {

                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this,
                                    candidateDestinationCoordinate,pieceOnCandidate)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null){

                    if (board.getEnPassantPawn().getPiecePos() == (this.piecePos + (this.pieceAlliance.getOppositeDirection()))){

                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){

                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }

            } else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.FIRST_COLUMN[this.piecePos] && this.pieceAlliance.isWhite()) ||
                    (BoardUtils.EIGHTH_COLUMN[this.piecePos] && this.pieceAlliance.isBlack()))) {


                if (board.getTile(candidateDestinationCoordinate).isTileOccupied()) {
                    final Piece pieceOnCandidate = board.getTile(candidateDestinationCoordinate).getPiece();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)){
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnAttackMove(board, this,
                                    candidateDestinationCoordinate,pieceOnCandidate)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }


                }else if (board.getEnPassantPawn() != null){
                    if (board.getEnPassantPawn().getPiecePos() == (this.piecePos - (this.pieceAlliance.getOppositeDirection()))){

                        final Piece pieceOnCandidate = board.getEnPassantPawn();
                        if (this.pieceAlliance != pieceOnCandidate.getPieceAlliance()){

                            legalMoves.add(new Move.PawnEnPassantAttackMove(board, this,
                                    candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                }
            }
        }

        return Collections.unmodifiableList(legalMoves);
    }
    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
    @Override
    public Pawn movePiece (Move move){
        return new Pawn(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate() );
    }
    public Piece getPromotionPiece(){
        return new Queen (this.pieceAlliance, this.piecePos, false);
    }
}
