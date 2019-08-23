package main.java.player.ai;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.Move;
import main.java.player.MoveTransition;

public class Minimax implements MoveStrategy {
    private  final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private final Alliance AI;

    public Minimax(final int searchDepth, Alliance AI) {
        this.boardEvaluator = new TestEval();
        this.searchDepth = searchDepth;
        this.AI = AI;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINGKING with depth = " + this.searchDepth);
        int numMoves = board.currentPlayer().getLegalMoves().size();
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                currentValue = board.currentPlayer().getAliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth-1) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth-1);
                if ( board.currentPlayer().getAliance().isWhite() && currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                } else if (board.currentPlayer().getAliance().isBlack() && currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue; bestMove = move;
                }
            }
        }
        final long executionTime = System.currentTimeMillis() - startTime;
        return bestMove;
    }

    public int min(final Board board, final int depth){
        if (depth == 0 || isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth, AI);
        }
        int lowestSeenValue = Integer.MAX_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getTransitionBoard(), depth -1);
                if (currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;
    }

    private boolean isEndGameScenario(final Board board) {
        return board.currentPlayer().isInCheckMate() || board.currentPlayer().isInStaleMate();
    }

    public int max (final Board board, final int depth){
        if (depth == 0|| isEndGameScenario(board)){
            return this.boardEvaluator.evaluate(board, depth, AI);
        }
        int highestSeenValue = Integer.MIN_VALUE;
        for (final Move move : board.currentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getTransitionBoard(), depth -1);
                if (currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
}
