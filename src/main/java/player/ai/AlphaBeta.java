package main.java.player.ai;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import main.java.board.Board;
import main.java.board.BoardUtils;
import main.java.board.Move;
import main.java.player.MoveTransition;
import main.java.player.Player;

import java.util.Collection;
import java.util.Comparator;
import java.util.Observable;

import static main.java.board.BoardUtils.mvvlva;

public class AlphaBeta extends Observable implements MoveStrategy {

    private  final BoardEvaluator evaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private long executionTime;
    private int quiescenceCount;
    private static final int MAX_QUIESCENCE = 5000*10;

    private enum MoveSorter {

        STANDARD {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
                        .compareTrueFirst(move1.isCastilingMove(), move2.isCastilingMove())
                        .compare(mvvlva(move2), mvvlva(move1))
                        .result()).immutableSortedCopy(moves);
            }
        },
        EXPENSIVE {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from((Comparator<Move>) (move1, move2) -> ComparisonChain.start()
                        .compareTrueFirst(BoardUtils.kingThreat(move1), BoardUtils.kingThreat(move2))
                        .compareTrueFirst(move1.isCastilingMove(), move2.isCastilingMove())
                        .compare(mvvlva(move2), mvvlva(move1))
                        .result()).immutableSortedCopy(moves);
            }
        };

        abstract  Collection<Move> sort(Collection<Move> moves);
    }


    public AlphaBeta(final int searchDepth) {
        this.evaluator = new TestEval();
        this.searchDepth = searchDepth;
        this.boardsEvaluated = 0;
        this.quiescenceCount = 0;
    }

    @Override
    public String toString() {
        return "StockAlphaBeta";
    }

    public long getNumBoardsEvaluated() {
        return this.boardsEvaluated;
    }

    @Override
    public Move execute(Board board) {
        final long startTime = System.currentTimeMillis();
        final Player currentPlayer = board.currentPlayer();
        Move bestMove = null;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.currentPlayer() + " THINKING with depth = " + this.searchDepth);
        int moveCounter = 1;
        int numMoves = board.currentPlayer().getLegalMoves().size();
        Collection<Move> sortedMoves = MoveSorter.EXPENSIVE.sort((board.currentPlayer().getLegalMoves()));
        for (final Move move : sortedMoves) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            this.quiescenceCount = 0;
            final String s;
            if (moveTransition.getMoveStatus().isDone()) {
                final long candidateMoveStartTime = System.nanoTime();
                currentValue = currentPlayer.getAliance().isWhite() ?
                        min(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getTransitionBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if (currentPlayer.getAliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                    if(moveTransition.getTransitionBoard().blackPlayer().isInCheckMate()) {
                        break;
                    }
                }
                else if (currentPlayer.getAliance().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                    if(moveTransition.getTransitionBoard().whitePlayer().isInCheckMate()) {
                        break;
                    }
                }

                final String quiescenceInfo = " " + score(currentPlayer, highestSeenValue, lowestSeenValue) + " q: " +this.quiescenceCount;
                s = "\t" + toString() + "(" +this.searchDepth+ "), m: (" +moveCounter+ "/" +numMoves+ ") " + move + ", best:  " + bestMove

                        + quiescenceInfo + ", t: " +calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
            } else {
                s = "\t" + toString() + ", m: (" +moveCounter+ "/" +numMoves+ ") " + move + " is illegal! best: " +bestMove;
            }
            System.out.println(s);
            setChanged();
            notifyObservers(s);
            moveCounter++;
        }

        this.executionTime = System.currentTimeMillis() - startTime;
        final String result = board.currentPlayer() + " SELECTS " +bestMove+ " [#boards evaluated = " +this.boardsEvaluated+
                " time taken = " +this.executionTime/1000+ " rate = " +(1000 * ((double)this.boardsEvaluated/this.executionTime));
        System.out.printf("%s SELECTS %s [#boards evaluated = %d, time taken = %d ms, rate = %.1f\n", board.currentPlayer(),
                bestMove, this.boardsEvaluated, this.executionTime, (1000 * ((double)this.boardsEvaluated/this.executionTime)));
        setChanged();
        notifyObservers(result);
        return bestMove;
    }

    private static String score(final Player currentPlayer,
                                final int highestSeenValue,
                                final int lowestSeenValue) {

        if(currentPlayer.getAliance().isWhite()) {
            return "[score: " +highestSeenValue + "]";
        } else if(currentPlayer.getAliance().isBlack()) {
            return "[score: " +lowestSeenValue+ "]";
        }
        throw new RuntimeException("bad bad boy!");
    }

    private int max(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtils.isEndGame(board)) {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : MoveSorter.STANDARD.sort((board.currentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, min(moveTransition.getTransitionBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), currentHighest, lowest));
                if (currentHighest >= lowest) {
                    return lowest;
                }
            }
        }
        return currentHighest;
    }

    private int min(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || BoardUtils.isEndGame(board)) {
            this.boardsEvaluated++;
            return this.evaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : MoveSorter.STANDARD.sort((board.currentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.currentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, max(moveTransition.getTransitionBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    return highest;
                }
            }
        }
        return currentLowest;
    }

    private int calculateQuiescenceDepth(final MoveTransition moveTransition,
                                         final int depth) {
        if(depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
            int activityMeasure = 0;
            if (moveTransition.getTransitionBoard().currentPlayer().isInCheck()) {
                activityMeasure += 1;
            }
            for(final Move move: BoardUtils.lastNMoves(moveTransition.getTransitionBoard(), 2)) {
                if(move.isAttack()) {
                    activityMeasure += 1;
                }
            }
            if(activityMeasure >= 2) {
                this.quiescenceCount++;
                return 1;
            }
        }
        return depth - 1;
    }

    private static String calculateTimeTaken(final long start, final long end) {
        final long timeTaken = (end - start) / 1000000;
        return timeTaken + " ms";
    }

}