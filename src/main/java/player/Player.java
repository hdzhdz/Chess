package main.java.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import main.java.Alliance;
import main.java.board.Board;
import main.java.board.Move;
import main.java.pieces.King;
import main.java.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Player {
    
    protected  final Board board;
    protected  final King playerKing;
    protected  final Collection<Move> legalMoves;

    private final boolean isInCheck;

    Player (final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves){
        this.board = board;
        this.playerKing = ebstablishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(legalMoves, opponentMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePos(), opponentMoves).isEmpty();
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePos, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePos == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return Collections.unmodifiableList(attackMoves);
    }

    private King ebstablishKing(){
        for (final Piece piece : getActivePieces()) {
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("False Board");
    }
    public  boolean isMoveLegal (final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck &&!hasEscapeMoves();
    }

    protected boolean hasEscapeMoves(){
        for (final Move move : this.legalMoves){
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isInStaleMate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove (final Move move){
        if (!isMoveLegal((move))){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks =
                Player.calculateAttacksOnTile(transitionBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePos(), transitionBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    public abstract Collection<Piece> getActivePieces();

    public abstract Alliance getAliance();

    public abstract Player getOpponent();

    protected abstract  Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                              Collection<Move> opponentsLegals);
}
