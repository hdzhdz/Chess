package main.java.player.ai;

import main.java.board.Board;
import main.java.board.Move;

public interface MoveStrategy {


    Move execute(Board board);

}
