import main.java.board.Board;

import Jgui.Table;

public class JChess {

    public static void main (String[] args){
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table.get().show();
    }
}
