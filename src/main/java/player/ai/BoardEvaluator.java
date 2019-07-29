package main.java.player.ai;

import main.java.board.Board;

public interface BoardEvaluator {
    int evaluate (Board board, int depth);
}
