package main.java.player.ai;

import main.java.Alliance;
import main.java.board.Board;
import main.java.board.Move;
import main.java.pieces.Piece;
import main.java.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestEval implements BoardEvaluator {
    private static final int CHECK_BONUS = 0;
    private static final int CHECK_MATE_BONUS = 100000;
    private static int DEPTH_BONUS = 5;
    private static int CASTLE_BONUS = 10;

    @Override
    public int evaluate(final Board board, final int depth, Alliance AI) {
        //return pieceSquareMidGame(board, board.blackPlayer(),depth);
        //Board cop = board.copy();
        return mainEval(board, board.whitePlayer(), depth) - mainEval(board, board.blackPlayer(), depth) + checkAttacked(board, depth, AI);
    }

    private static int checkAttacked(Board board, int depth, Alliance AI) {
        if (depth != 0) {
            return 0;
        }
        // if the opponent have a piece that can attack the current player which the attacked piece has higher value
        // than the current player deduct that amount of value
        int score = 0;
        List<Piece> attackedPieceList = new ArrayList<>();
        //System.out.println(board.currentPlayer().getAliance().toString());
        if (AI.isWhite()) {
            for (Move move : board.blackPlayer().getLegalMoves()) {
                if (move.isAttack()) {
                    if (!attackedPieceList.contains(move.getAttackedPiece())) {
                        int attackPiece = move.getMovedPiece().getPieceValue();
                        int attackedPiece = move.getAttackedPiece().getPieceValue();
                        if (attackPiece - attackedPiece < 0) {
                            score = score - attackedPiece;
                        }
                        //System.out.println("black " + move.getMovedPiece().toString() + " attack " + move
                        // .getAttackedPiece().toString() + " score: " + score + " current player: " + board.currentPlayer().getAliance().toString());

                    }
                }
            }
        }
        if (AI.isBlack()) {
            for (Move move : board.whitePlayer().getLegalMoves()) {
                if (move.isAttack()) {
                    if (!attackedPieceList.contains(move.getAttackedPiece())) {
                        int attackPiece = move.getMovedPiece().getPieceValue();
                        int attackedPiece = move.getAttackedPiece().getPieceValue();
                        if (attackPiece - attackedPiece < 0) {
                            score = score + attackedPiece;
                        }
                        //System.out.println("white " + move.getMovedPiece().toString() + " attack " + move
                        // .getAttackedPiece().toString() + " score: " + score + " current player: " + board.currentPlayer().getAliance().toString());

                    }
                }
            }
        }
        return score;
    }

    private int mainEval(Board board, Player player, int depth) {
        return midGameEval(board, player, depth);
    }

    private int midGameEval(Board board, Player player, int depth) {
        int score = 0;
        score += pieceValueMidGame(board, player, depth);
        score += pieceSquareMidGame(board, player, depth);
        score += imbalance(board, player, depth);
        score += bishopPair(board, player, depth);
        score += pawnMidGame(board, player, depth);
        score += castled(player) + depthBonus(depth) + mobility(player) + checkmate(player, depth);
        return score;
    }

    private static int castled(Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private int checkmate(Player player, int depth) {
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(Player player) {
        return player.getLegalMoves().size() / 2;
    }

    private int pawnMidGame(Board board, Player player, int depth) {
        int score = 0;
        List<Integer> playerPawnPos = playerPawnPos(board, player, depth);
        Map<Integer, Alliance> pawnPos = pawnPos(board, player, depth);
        //score -= isolatedPawn(playerPawnPos) * 3;
        //score -= backwardPawn(playerPawnPos) * 2;
        //score -= doublePawn(pawnPos) * 11;
        score -= 5 * weakUnopposedPawn(player, pawnPos, playerPawnPos);
        // need to check !!
        score += connected(board, player, depth, playerPawnPos);
        return score;
    }

    private int connected(Board board, Player player, int depth, List<Integer> playerPawnPos) {
        int connected = 0;
        int countSupported = 0;
        int countPhalanx = 0;
        int backWardCount = 0;
        int isolatedCount = 0;
        for (int pawn : playerPawnPos) {
            if ((7 < pawn && pawn < 16) || (47 < pawn && pawn < 56)) {
                break;
            }
            if (playerPawnPos.contains(pawn + 7)) {
                countSupported++;
            }
            if (playerPawnPos.contains(pawn + 9)) {
                countSupported++;
            }
            if (playerPawnPos.contains(pawn - 1)) {
                countPhalanx++;
            }
            if (playerPawnPos.contains(pawn + 1)) {
                countPhalanx++;
            }
            boolean isolated = true;
            boolean backWard = false;
            for (int pawnCheck : playerPawnPos) {
                int check = pawn - pawnCheck;
                int checkBackWard = pawn - pawnCheck;
                if (check != 0 && (check == 1 || check == -1)) {
                    isolated = false;
                }
                if (checkBackWard != 0 && (checkBackWard == 15 || checkBackWard == 17 || checkBackWard == 8)) {
                    backWard = true;
                }
            }
            if (isolated == true) {
                isolatedCount++;
                break;
            }
            if (backWard == true) {
                backWardCount++;
            }

        }
        if (countSupported != 0) {
            connected = countSupported;
        }
        connected = countPhalanx;
        int check = connected * 9 - isolatedCount * 5 - backWardCount * 6;
        return check;
    }


    private int weakUnopposedPawn(Player player, Map<Integer, Alliance> pawnPos, List<Integer> playerPawnPos) {
        int count = 0;
        Alliance playerAlliance = player.getAliance();
        // check oppose
        for (int playerPawn : playerPawnPos) {
            boolean oppose = false;
            int test = playerPawn;
            while (test < 64 && test > -1) {
                if (playerAlliance.isWhite()) {
                    test = test - 8;
                    if (pawnPos.containsKey(test) && pawnPos.get(test).isBlack()) {
                        oppose = true;
                    }
                }
                if (playerAlliance.isBlack()) {
                    test = test + 8;
                    if (pawnPos.containsKey(test) && pawnPos.get(test).isWhite()) {
                        oppose = true;
                    }
                }
            }
            if (oppose) {
                continue;
            }
            // not oppose check isolate and backward
            boolean isolated = true;
            boolean backWard = false;
            for (int pawnCheck : playerPawnPos) {
                int check = playerPawn - pawnCheck;
                int checkBackWard = playerPawn - pawnCheck;
                if (check != 0 && (check == 1 || check == -1)) {
                    isolated = false;
                }
                if (checkBackWard != 0 && (checkBackWard == 15 || checkBackWard == 17 || checkBackWard == 8)) {
                    backWard = true;
                }
            }
            if (isolated == true) {
                count++;
            } else {
                if (backWard) {
                    count++;
                }
            }
        }
        return count;
    }


    private int backwardPawn(List<Integer> playerPawnPos) {
        int count = 0;
        for (int pawn : playerPawnPos) {
            boolean isolated = true;
            boolean backWard = false;
            for (int pawnCheck : playerPawnPos) {
                int check = pawn - pawnCheck;
                int checkBackWard = pawn - pawnCheck;
                if (check != 0 && (check == 1 || check == -1)) {
                    isolated = false;
                }
                if (checkBackWard != 0 && (checkBackWard == 15 || checkBackWard == 17 || checkBackWard == 8)) {
                    backWard = true;
                }
            }
            if (isolated == true) {
                continue;
            }
            if (backWard == true) {
                count++;
            }
        }
        return count;
    }

    private int isolatedPawn(List<Integer> playerPawnPos) {
        int count = 0;
        for (int pawn : playerPawnPos) {
            boolean isolated = true;
            for (int pawnCheck : playerPawnPos) {
                int check = pawn - pawnCheck;
                if (check != 0 && (check == 1 || check == -1)) {
                    isolated = false;
                }
            }
            if (isolated == true) {
                count++;
            }
        }
        return count;
    }

    private Map<Integer, Alliance> pawnPos(Board board, Player player, int depth) {
        Map<Integer, Alliance> pawnPos = new HashMap<>();
        for (final Piece piece : player.getActivePieces()) {
            if (piece.getPieceType().isPawns()) {
                pawnPos.put(piece.getPiecePos(), player.getAliance());
            }
        }
        for (final Piece piece : player.getOpponent().getActivePieces()) {
            if (piece.getPieceType().isPawns()) {
                pawnPos.put(piece.getPiecePos(), player.getOpponent().getAliance());
            }
        }
        return pawnPos;
    }

    private List<Integer> playerPawnPos(Board board, Player player, int depth) {
        List<Integer> pawnPos = new ArrayList<>();
        for (final Piece piece : player.getActivePieces()) {
            if (piece.getPieceType().isPawns()) {
                pawnPos.add(piece.getPiecePos());
            }
        }
        return pawnPos;
    }


    private int bishopPair(Board board, Player player, int depth) {
        int count = 0;
        for (final Piece piece : player.getActivePieces()) {
            if (piece.getPieceType().isBishop()) {
                count++;
            }
        }
        if (count == 2) {
            return 1000;
        }
        return 0;
    }

    // TODO : fix these values
    private int imbalance(Board board, Player player, int depth) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()) {
            if (piece.getPieceType().isRook()) {
                //pieceValueScore += 204;
                pieceValueScore += 1852;
            }
            if (piece.getPieceType().isBishop()) {
                //pieceValueScore += 1503;
                pieceValueScore += 1503;
            }
            if (piece.getPieceType().isKnight()) {
                //pieceValueScore += 2461;
                pieceValueScore += 1503;
            }
            if (piece.getPieceType().isQueen()) {
                //pieceValueScore += 1852;
                pieceValueScore += 2461;
            }
            if (piece.getPieceType().isPawns()) {
                pieceValueScore += 128;
            }
        }
        for (final Piece piece : player.getOpponent().getActivePieces()) {
            if (piece.getPieceType().isRook()) {
                //pieceValueScore -= 204;
                pieceValueScore -= 1852;
            }
            if (piece.getPieceType().isBishop()) {
                //pieceValueScore -= 1503;
                pieceValueScore -= 1503;
            }
            if (piece.getPieceType().isKnight()) {
                //pieceValueScore -= 2461;
                pieceValueScore -= 1503;
            }
            if (piece.getPieceType().isQueen()) {
                //pieceValueScore -= 1852;
                pieceValueScore -= 2461;
            }
            if (piece.getPieceType().isPawns()) {
                pieceValueScore -= 128;
            }
        }
        return 0;
    }

    private int pieceSquareMidGame(Board board, Player player, int depth) {
        if ((board.getBlackPieces().size() + board.getWhitePieces().size()) > 16) {
            int score = 0;
            if (player.getAliance().isWhite()) {
                int[] knightPos =
                        {-50, -40, -30, -30, -30, -30, -40, -50,
                                -40, -20, 0, 0, 0, 0, -20, -40,
                                -30, 0, 10, 15, 15, 10, 0, -30,
                                -30, 5, 15, 20, 20, 15, 5, -30,
                                -30, 0, 15, 20, 20, 15, 0, -30,
                                -30, 5, 10, 15, 15, 10, 5, -30,
                                -40, -20, 0, 5, 5, 0, -20, -40,
                                -50, -40, -30, -30, -30, -30, -40, -50};

                int[] pawnPos =
                        {0, 0, 0, 0, 0, 0, 0, 0,
                                50, 50, 50, 50, 50, 50, 50, 50,
                                10, 10, 20, 30, 30, 20, 10, 10,
                                5, 5, 10, 25, 25, 10, 5, 5,
                                0, 0, 0, 20, 20, 0, 0, 0,
                                5, -5, -10, 0, 0, -10, -5, 5,
                                5, 10, 10, -200, -200, 10, 10, 5,
                                0, 0, 0, 0, 0, 0, 0, 0};
                int[] bishopPos =
                        {-20, -10, -10, -10, -10, -10, -10, -20,
                                -10, 0, 0, 0, 0, 0, 0, -10,
                                -10, 0, 5, 10, 10, 5, 0, -10,
                                -10, 5, 5, 10, 10, 5, 5, -10,
                                -10, 0, 10, 10, 10, 10, 0, -10,
                                -10, 10, 10, 10, 10, 10, 10, -10,
                                -10, 5, 0, 0, 0, 0, 5, -10,
                                -20, -10, -10, -10, -10, -10, -10, -20};

                int[] rookPos =
                        {0, 0, 0, 0, 0, 0, 0, 0,
                                5, 10, 10, 10, 10, 10, 10, 5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                0, 0, 0, 5, 5, 0, 0, 0};


                int[] queenPos =
                        {-20, -10, -10, -5, -5, -10, -10, -20,
                                -10, 0, 0, 0, 0, 0, 0, -10,
                                -10, 0, 5, 5, 5, 5, 0, -10,
                                -5, 0, 5, 5, 5, 5, 0, -5,
                                0, 0, 5, 5, 5, 5, 0, -5,
                                -10, 5, 5, 5, 5, 5, 0, -10,
                                -10, 0, 5, 0, 0, 0, 0, -10,
                                -20, -10, -10, -5, -5, -10, -10, -20};
                int[] kingPos =
                        {-30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -20, -30, -30, -40, -40, -30, -30, -20,
                                -10, -20, -20, -20, -20, -20, -20, -10,
                                20, 20, 0, 0, 0, 0, 20, 20,
                                20, 30, 10, 0, 0, 10, 30, 20};
                for (Piece piece : player.getActivePieces()) {
                    if (piece.getPieceType().isKing()) {
                        //System.out.println("KING " +kingPos[piece.getPiecePos()] );
                        score += kingPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isQueen()) {
                        score += queenPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isRook()) {
                        score += rookPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isBishop()) {
                        score += bishopPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isPawns()) {
                        score += pawnPos[piece.getPiecePos()];
                        //System.out.println("PAWN "+ pawnPos[piece.getPiecePos()]);
                    }
                    if (piece.getPieceType().isKnight()) {
                        score += knightPos[piece.getPiecePos()];
                    }
                }
            }
            if (player.getAliance().isBlack()) {
                int[] knightPos =
                        {-50, -40, -30, -30, -30, -30, -40, -50,
                                -40, -20, 0, 5, 5, 0, -20, -40,
                                -30, 5, 10, 15, 15, 10, 5, -30,
                                -30, 0, 15, 20, 20, 15, 0, -30,
                                -30, 5, 15, 20, 20, 15, 5, -30,
                                -30, 0, 10, 15, 15, 10, 0, -30,
                                -40, -20, 0, 0, 0, 0, -20, -40,
                                -50, -40, -30, -30, -30, -30, -40, -50};
                int[] pawnPos =
                        {0, 0, 0, 0, 0, 0, 0, 0,
                                5, 10, 10, -600, -600, 10, 10, 5,
                                5, -5, -10, -10, -10, -10, -5, 5,
                                0, 0, 0, 20, 20, 0, 0, 0,
                                5, 5, 10, 25, 25, 10, 5, 5,
                                10, 10, 20, 30, 30, 20, 10, 10,
                                50, 50, 50, 50, 50, 50, 50, 50,
                                0, 0, 0, 0, 0, 0, 0, 0};
                int[] bishopPos =
                        {-20, -10, -10, -10, -10, -10, -10, -20,
                                -10, 5, 0, 0, 0, 0, 5, -10,
                                -10, 10, 10, 10, 10, 10, 10, -10,
                                -10, 0, 10, 10, 10, 10, 0, -10,
                                -10, 5, 5, 10, 10, 5, 5, -10,
                                -10, 0, 5, 10, 10, 5, 0, -10,
                                -10, 0, 0, 0, 0, 0, 0, -10,
                                -20, -10, -10, -10, -10, -10, -10, -20};
                int[] rookPos =
                        {0, 0, 0, 5, 5, 0, 0, 0,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                -5, 0, 0, 0, 0, 0, 0, -5,
                                5, 10, 10, 10, 10, 10, 10, 5,
                                0, 0, 0, 0, 0, 0, 0, 0,
                        };
                int[] queenPos =
                        {-20, -10, -10, -5, -5, -10, -10, -20,
                                -10, 0, 5, 0, 0, 0, 0, -10,
                                -10, 5, 5, 5, 5, 5, 0, -10,
                                0, 0, 5, 5, 5, 5, 0, -5,
                                -5, 0, 5, 5, 5, 5, 0, -5,
                                -10, 0, 5, 5, 5, 5, 0, -10,
                                -10, 0, 0, 0, 0, 0, 0, -10,
                                -20, -10, -10, -5, -5, -10, -10, -20};
                int[] kingPos =
                        {
                                20, 30, 10, 0, 0, 10, 30, 20,
                                20, 20, 0, 0, 0, 0, 20, 20,
                                -10, -20, -20, -20, -20, -20, -20, -10,
                                -20, -30, -30, -40, -40, -30, -30, -20,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30,
                                -30, -40, -40, -50, -50, -40, -40, -30};
                for (Piece piece : player.getActivePieces()) {
                    if (piece.getPieceType().isKing()) {
                        score += kingPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isQueen()) {
                        score += queenPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isRook()) {
                        score += rookPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isBishop()) {
                        score += bishopPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isPawns()) {
                        score += pawnPos[piece.getPiecePos()];
                    }
                    if (piece.getPieceType().isKnight()) {
                        score += knightPos[piece.getPiecePos()];
                    }
                }
            }
            //System.out.println("TESTTTTTT");
            return score / 3;
        }
        return 0;
    }

    private int pieceValueMidGame(Board board, Player player, int depth) {
        int pieceValueScore = 0;
        for (final Piece piece : player.getActivePieces()) {
            pieceValueScore += piece.getPieceValue();
        }
        return pieceValueScore;

    }
}