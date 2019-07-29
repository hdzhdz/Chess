package main.java.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import main.java.pieces.Piece;
import main.java.player.MoveTransition;

import java.util.*;

public class BoardUtils {

    public static final boolean[] FIRST_COLUMN = initColumn(0);
    public static final boolean[] SECOND_COLUMN = initColumn(1);
    public static final boolean[] SEVENTH_COLUMN = initColumn(6);
    public static final boolean[] EIGHTH_COLUMN = initColumn(7);

    public static final boolean[] FIRST_ROW = initRow(0);
    public static final boolean[] SECOND_ROW = initRow(8);
    public static final boolean[] SEVENTH_ROW = initRow(48);
    public static final boolean[] EIGHTH_ROW = initRow(56);

    public static final List<String> ALGEBRAIC_NOTATION = initializeAlgebraicNotation();
    public static final Map<String, Integer> PROGRAM_NOTATION = initializeNumPosition();
    public static final Map<String, Integer> POSITION_TO_COORDINATE = initializePositionToCoordinateMap();

    public static final int START_TILE_INDEX = 0;
    public static final int NUM_TILES = 64;
    public static final int NUM_TILES_PER_ROW = 8;

    private static boolean[] initColumn(int columnNumber) {

        final boolean[] column = new boolean[NUM_TILES];
        while (columnNumber < NUM_TILES) {
            column[columnNumber] = true;
            columnNumber += NUM_TILES_PER_ROW;
        }
        return column;
    }

    private static boolean[] initRow(int rowNumber) {
        final boolean[] row = new boolean[NUM_TILES];
        row[rowNumber] = true;
        rowNumber++;
        while (rowNumber % NUM_TILES_PER_ROW != 0) {
            row[rowNumber] = true;
            rowNumber++;
        }
        return row;
    }

    private BoardUtils() {
        throw new RuntimeException("Can't create");
    }

    public static boolean isValidCoordinate(int coordinate) {
        return coordinate >= 0 && coordinate < NUM_TILES;
    }

    private static Map<String, Integer> initializePositionToCoordinateMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap<>();
        for (int i = START_TILE_INDEX; i < NUM_TILES; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return ImmutableMap.copyOf(positionToCoordinate);
    }

    private static List<String> initializeAlgebraicNotation() {
        return ImmutableList.copyOf(new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        });
    }

    private static Map<String, Integer> initializeNumPosition() {
        Map<String, Integer> map = new HashMap<>();
        map.put("A8", 0);map.put("B8", 1);map.put("C8", 2);map.put("D8", 3);map.put("E8", 4);map.put("F8", 5);map.put("G8", 6);map.put("H8", 7);
        map.put("A7", 8);map.put("B7", 9);map.put("C7", 10);map.put("D7", 11);map.put("E7", 12);map.put("F7", 13);map.put("G7", 14);map.put("H7", 15);
        map.put("A6", 16);map.put("B6", 17);map.put("C6", 18);map.put("D6", 19);map.put("E6", 20);map.put("F6", 21);map.put("G6", 22);map.put("H6", 23);
        map.put("A5", 24);map.put("B5", 25);map.put("C5", 26);map.put("D5", 27);map.put("E5", 28);map.put("F5", 29);map.put("G5", 30);map.put("H5", 31);
        map.put("A4", 32);map.put("B4", 33);map.put("C4", 34);map.put("D4", 35);map.put("E4", 36);map.put("F4", 37);map.put("G4", 38);map.put("H4", 39);
        map.put("A5", 40);map.put("B3", 41);map.put("C3", 42);map.put("D3", 43);map.put("E3", 44);map.put("F3", 45);map.put("G3", 46);map.put("H3", 47);
        map.put("A2", 48);map.put("B2", 49);map.put("C2", 50);map.put("D2", 51);map.put("E2", 52);map.put("F2", 53);map.put("G2", 54);map.put("H2", 55);
        map.put("A1", 56);map.put("B1", 57);map.put("C1", 58);map.put("D1", 59);map.put("E1", 60);map.put("F1", 61);map.put("G1", 62);map.put("H1", 63);
        return map;
    }
    public static int getNumPos (String algebraicPos){
        int pos = PROGRAM_NOTATION.get(algebraicPos);
        return pos;
    }
    public static boolean isValidTileCoordinate(final int coordinate) {
        return coordinate >= START_TILE_INDEX && coordinate < NUM_TILES;
    }

    public static int getCoordinateAtPosition(final String position) {
        return POSITION_TO_COORDINATE.get(position);
    }

    public static String getPositionAtCoordinate(final int coordinate) {
        return ALGEBRAIC_NOTATION.get(coordinate);
    }

    public static int mvvlva(final Move move) {
        final Piece movingPiece = move.getMovedPiece();
        if(move.isAttack()) {
            final Piece attackedPiece = move.getAttackedPiece();
            return (attackedPiece.getPieceValue() - movingPiece.getPieceValue() +  Piece.PieceType.KING.getPieceValue()) * 100;
        }
        return Piece.PieceType.KING.getPieceValue() - movingPiece.getPieceValue();
    }
    public static boolean kingThreat(final Move move) {
        final Board board = move.getBoard();
        final MoveTransition transition = board.currentPlayer().makeMove(move);
        return transition.getTransitionBoard().currentPlayer().isInCheck();
    }
    public static boolean isEndGame(final Board board) {
        return board.currentPlayer().isInCheckMate() ||
                board.currentPlayer().isInStaleMate();
    }
    public static List<Move> lastNMoves(final Board board, int N) {
        final List<Move> moveHistory = new ArrayList<>();
        Move currentMove = board.getTransitionMove();
        int i = 0;
        while(currentMove != Move.MoveFactory.getNullMove() && i < N) {
            moveHistory.add(currentMove);
            currentMove = currentMove.getBoard().getTransitionMove();
            i++;
        }
        return Collections.unmodifiableList(moveHistory);
    }
}
