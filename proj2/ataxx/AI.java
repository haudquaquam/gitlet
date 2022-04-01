/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import java.util.ArrayList;
import java.util.Random;

import static ataxx.PieceColor.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

/** A Player that computes its own moves.
 *  @author Rae Xin
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 4;
    /** A position magnitude indicating a win (for red if positive, blue
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. SEED is used to initialize
     *  a random-number generator for use in move computations.  Identical
     *  seeds produce identical behaviour. */
    AI(Game game, PieceColor myColor, long seed) {
        super(game, myColor);
        _random = new Random(seed);
    }

    @Override
    boolean isAuto() {
        return true;
    }

    @Override
    String getMove() {
        if (!getBoard().canMove(myColor())) {
            game().reportMove(Move.pass(), myColor());
            return "-";
        }
        Main.startTiming();
        Move move = findMove();
        Main.endTiming();
        game().reportMove(move, myColor());
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(getBoard());
        _lastFoundMove = null;
        if (myColor() == RED) {
            minMax(b, MAX_DEPTH, true, 1, -INFTY, INFTY);
        } else {
            minMax(b, MAX_DEPTH, true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to the findMove method
     *  above. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */

    private int minMax(Board board, int depth, boolean saveMove, int sense,
                       int alpha, int beta) {

        if (depth == 0 || board.getWinner() != null) {
            return staticScore(board, WINNING_VALUE + depth);
        }

        Move best = null;
        int bestScore;

        if (sense == 1) {
            bestScore = -INFTY;
            ArrayList<Move> allMyMoves = findLegalMoves(board, RED);
            for (Move currentMove : allMyMoves) {
                Board newBoardCopy = new Board(board);
                newBoardCopy.makeMove(currentMove);
                int currentScore = minMax(newBoardCopy, depth - 1,
                        false, -1, alpha, beta);
                if (currentScore > bestScore || currentMove.isPass()) {
                    best = currentMove;
                    bestScore = currentScore;
                    alpha = max(alpha, bestScore);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        } else {
            bestScore = INFTY;
            ArrayList<Move> allMyMoves = findLegalMoves(board, BLUE);
            for (Move currentMove : allMyMoves) {
                Board newBoardCopy = new Board(board);
                newBoardCopy.makeMove(currentMove);
                int currentScore = minMax(newBoardCopy, depth - 1,
                        false, 1, alpha, beta);
                if (currentScore < bestScore || currentMove.isPass()) {
                    best = currentMove;
                    bestScore = currentScore;
                    beta = min(beta, bestScore);
                    if (alpha >= beta) {
                        break;
                    }
                }
            }
        }
        if (saveMove) {
            _lastFoundMove = best;
        }
        return bestScore;
    }

    /** Helper method that finds all legal moves of a COLOR on a BOARD. Return
     * ArrayList of Moves that are legal.
     * **/
    private ArrayList<Move> findLegalMoves(Board board, PieceColor color) {
        ArrayList<Move> returnArray = new ArrayList<>();
        for (char c = 'a'; c <= 'g'; c++) {
            for (char r = '1'; r <= '7'; r++) {
                if (board.get(c, r).compareTo(color) == 0) {
                    for (char c2 = 'a'; c2 <= 'g'; c2++) {
                        for (char r2 = '1'; r2 <= '7'; r2++) {
                            Move currentMove = Move.move(c, r, c2, r2);
                            if (board.legalMove(currentMove)) {
                                returnArray.add(currentMove);
                            }
                        }
                    }
                }
            }
        }
        if (returnArray.isEmpty()) {
            returnArray.add(Move.pass());
        }
        return returnArray;
    }

    /** Return a heuristic value for BOARD.  This value is +- WINNINGVALUE in
     *  won positions, and 0 for ties. */
    private int staticScore(Board board, int winningValue) {
        PieceColor winner = board.getWinner();
        if (winner != null) {
            return switch (winner) {
                case RED -> winningValue;
                case BLUE -> -winningValue;
                default -> 0;
            };
        }
        return board.redPieces() - board.bluePieces();
    }

    /** Pseudo-random number generator for move computation. */
    private Random _random = new Random();
}