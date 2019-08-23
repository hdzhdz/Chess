package main.java.player.ai;

import main.java.Alliance;
import main.java.board.Board;

public interface BoardEvaluator {
    int evaluate (Board board, int depth, Alliance AI);
}
