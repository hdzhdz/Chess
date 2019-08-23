package main.java.player.ai;

import main.java.Alliance;
import main.java.board.Board;
import main.java.pieces.Piece;
import main.java.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator {
    private static  final int CHECK_BONUS = 10;
    private static final int CHECK_MATE_BONUS = 5000;
    private static int DEPTH_BONUS = 100;
    private static int CASTLE_BONUS = 200;

    @Override
    public int evaluate(final Board board, final int depth, Alliance AI) {
        return scorePlayer(board, board.whitePlayer(), depth) - scorePlayer(board,board.blackPlayer(), depth);
    }

    private int scorePlayer(final Board board, final Player player, final int depth) {
        return pieceValue(player) +
                mobility(player) +
                check(player) +
                checkmate(player, depth) +
                castled(player);
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheck() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()){
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;
    }


}
