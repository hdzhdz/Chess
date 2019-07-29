package main.java;

import main.java.board.BoardUtils;
import main.java.player.BlackPlayer;
import main.java.player.Player;
import main.java.player.WhitePlayer;

public enum Alliance {
    WHITE{
        @Override
        public int getDirection(){
            return  -1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.FIRST_ROW[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public int getOppositeDirection() {
            return 1;
        }
    },
    BLACK{
        @Override
        public int getDirection(){
            return  1;
        }
        @Override
        public int getOppositeDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(int position) {
            return BoardUtils.EIGHTH_ROW[position];
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };

    public abstract int getDirection();
    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract boolean isPawnPromotionSquare (int Position);

    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);

    public abstract int getOppositeDirection();
}
