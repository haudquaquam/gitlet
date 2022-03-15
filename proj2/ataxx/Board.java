/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Formatter;

import java.util.function.Consumer;

import static ataxx.PieceColor.*;
import static ataxx.GameException.error;
import static java.lang.Math.abs;
import static java.lang.Math.max;

/** An Ataxx board.   The squares are labeled by column (a char value between
 *  'a' - 2 and 'g' + 2) and row (a char value between '1' - 2 and '7'
 *  + 2) or by linearized index, an integer described below.  Values of
 *  the column outside 'a' and 'g' and of the row outside '1' to '7' denote
 *  two layers of border squares, which are always blocked.
 *  This artificial border (which is never actually printed) is a common
 *  trick that allows one to avoid testing for edge conditions.
 *  For example, to look at all the possible moves from a square, sq,
 *  on the normal board (i.e., not in the border region), one can simply
 *  look at all squares within two rows and columns of sq without worrying
 *  about going off the board. Since squares in the border region are
 *  blocked, the normal logic that prevents moving to a blocked square
 *  will apply.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Rae Xin
 */
class Board {

    /** Number of squares on a side of the board. */
    static final int SIDE = Move.SIDE;

    /** Length of a side + an artificial 2-deep border region.
     * This is unrelated to a move that is an "extend". */
    static final int EXTENDED_SIDE = Move.EXTENDED_SIDE;

    /** Number of consecutive non-extending moves before game ends. */
    static final int JUMP_LIMIT = 25;

    /** A new, cleared board in the initial configuration. */
    Board() {
        _board = new PieceColor[EXTENDED_SIDE * EXTENDED_SIDE];
        setNotifier(NOP);
        clear();
    }

    /** A board whose initial contents are copied from BOARD0, but whose
     *  undo history is clear, and whose notifier does nothing. */
    Board(Board board0) {
        _board = board0._board.clone();
        _allMoves = new ArrayList<>();
        _undoPieces = new Stack<>();
        _undoSquares = new Stack<>();
        _whoseMove = board0.whoseMove();
        _numJumps = board0.numJumps();
        _totalOpen = board0.totalOpen();
        _winner = board0.getWinner();
        _gameStarted = board0._gameStarted;
        _numPieces[BLUE.ordinal()] = board0.numPieces(BLUE);
        _numPieces[RED.ordinal()] = board0.numPieces(RED);
        setNotifier(NOP);
    }

    /** Return the linearized index of square COL ROW. */
    static int index(char col, char row) {
        return (row - '1' + 2) * EXTENDED_SIDE + (col - 'a' + 2);
    }

    /** Return the linearized index of the square that is DC columns and DR
     *  rows away from the square with index SQ. */
    static int neighbor(int sq, int dc, int dr) {
        return sq + dc + dr * EXTENDED_SIDE;
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions and no blocks. */
    void clear() {
        _whoseMove = RED;
        _numPieces = new int[]{0, 0, 0, 0};
        int ext = EXTENDED_SIDE;
        int doubleExt = EXTENDED_SIDE * 2;
        for (int i = 0; i < _board.length; i++) {
            _board[i] = null;
            int mod = i % ext;
            if (mod == 0 || mod == 1 || mod == ext - 1 || mod == ext - 2) {
                unrecordedSet(i, BLOCKED);
            } else if ((i < doubleExt) || (i >= _board.length - doubleExt)) {
                unrecordedSet(i, BLOCKED);
            } else {
                unrecordedSet(i, EMPTY);
            }
        }
        unrecordedSet('a', '7', RED);
        unrecordedSet('g', '7', BLUE);
        unrecordedSet('a', '1', BLUE);
        unrecordedSet('g', '1', RED);
        _totalOpen = SIDE * SIDE;
        _numJumps = 0;
        _gameStarted = false;
        _allMoves = new ArrayList<>();
        _winner = null;
        _undoSquares = new Stack<>();
        _undoPieces = new Stack<>();
        announce();
    }

    /** Return the winner, if there is one yet, and otherwise null.  Returns
     *  EMPTY in the case of a draw, which can happen as a result of there
     *  having been MAX_JUMPS consecutive jumps without intervening extends,
     *  or if neither player can move and both have the same number of pieces.*/
    PieceColor getWinner() {
        return _winner;
    }

    /** Return number of red pieces on the board. */
    int redPieces() {
        return numPieces(RED);
    }

    /** Return number of blue pieces on the board. */
    int bluePieces() {
        return numPieces(BLUE);
    }

    /** Return number of blocked pieces on the board. */
    int blockedPieces() {
        return numPieces(BLOCKED);
    }

    /** Return number of blocked pieces on the board. */
    int emptyPieces() {
        return numPieces(EMPTY);
    }

    /** Return number of COLOR pieces on the board. */
    int numPieces(PieceColor color) {
        return _numPieces[color.ordinal()];
    }

    /** Increment numPieces(COLOR) by K. */
    private void incrPieces(PieceColor color, int k) {
        _numPieces[color.ordinal()] += k;
    }

    /** The current contents of square CR, where 'a'-2 <= C <= 'g'+2, and
     *  '1'-2 <= R <= '7'+2.  Squares outside the range a1-g7 are all
     *  BLOCKED.  Returns the same value as get(index(C, R)). */
    PieceColor get(char c, char r) {
        return _board[index(c, r)];
    }

    /** Return the current contents of square with linearized index SQ. */
    PieceColor get(int sq) {
        return _board[sq];
    }

    /** Return row and column in String format of linearized index SQ. */
    public static String getCR(int sq) {
        int col = sq % EXTENDED_SIDE;
        int row = sq / EXTENDED_SIDE;
        col = (char) (col + 'a' - 2);
        row = (char) (row + '1' - 2);
        if (col <= 'g' && col >= 'a' && row <= '7' && row >= '0') {
            return Character.toString(col) + Character.toString(row);
        }
        return null;
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'g', and
     *  '1' <= R <= '7'. This operation is undoable. */
    private void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /** Set square with linearized index SQ to V.  This operation is
     *  undoable. */
    private void set(int sq, PieceColor v) {
        addUndo(sq);
        updatePieceCount(sq, v);
        _board[sq] = v;
    }

    /** Set square at C R to V (not undoable). This is used for changing
     * contents of the board without updating the undo stacks. */
    private void unrecordedSet(char c, char r, PieceColor v) {
        unrecordedSet(index(c, r), v);
    }

    /** Set square at linearized index SQ to V (not undoable). This is used
     * for changing contents of the board without updating the undo stacks. */
    private void unrecordedSet(int sq, PieceColor v) {
        updatePieceCount(sq, v);
        _board[sq] = v;
    }

    /** Update the counts for the PieceColor of SQ and V. */
    private void updatePieceCount(int sq, PieceColor v) {
        PieceColor former = get(sq);
        if (!(former == null)) {
            incrPieces(former, -1);
        }
        incrPieces(v, 1);

    }

    /** Return true iff MOVE is legal on the current board. */
    boolean legalMove(Move move) {
        if (move == null) {
            return false;
        }
        if (move.isPass()) {
            return true;
        }
        int moveDiff = moveDiff(move);
        PieceColor destination = get(move.toIndex());
        if (!(destination == EMPTY)) {
            return false;
        }
        if (moveDiff > 2 || moveDiff < 1) {
            return false;
        }
        if (get(move.col0(), move.row0()).compareTo(whoseMove()) != 0) {
            return false;
        }
        return true;
    }

    /** Returns maximum difference in indices of rows and columns in MOVE. */
    int moveDiff(Move move) {
        return max(abs(move.col0() - move.col1()), abs(move.row0() - move.row1()));
    }

    /** Return true iff C0 R0 - C1 R1 is legal on the current board. */
    boolean legalMove(char c0, char r0, char c1, char r1) {
        return legalMove(Move.move(c0, r0, c1, r1));
    }

    /** Return true iff player WHO can move, ignoring whether it is
     *  that player's move and whether the game is over. */
    boolean canMove(PieceColor who) {
        for (int i = 0; i < _board.length; i++) {
            for (int k = 0; k < _board.length; k++) {
                String moveString = getCR(i) + "-" + getCR(k);
                Move currentMove = Move.move(moveString);
                if (!(currentMove == null) && get(currentMove.fromIndex()) == who) {
                    if (legalMove(currentMove)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if the game is over. */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Return total number of moves and passes since the last
     *  clear or the creation of the board. */
    int numMoves() {
        return allMoves().size();
    }

    /** Return number of non-pass moves made in the current game since the
     *  last extend move added a piece to the board (or since the
     *  start of the game). Used to detect end-of-game. */
    int numJumps() {
        return _numJumps;
    }

    /** Assuming MOVE has the format "-" or "C0R0-C1R1", make the denoted
     *  move ("-" means "pass"). */
    void makeMove(String move) {
        if (move.equals("-")) {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(move.charAt(0), move.charAt(1), move.charAt(3),
                               move.charAt(4)));
        }
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        if (c0 == '-') {
            makeMove(Move.pass());
        } else {
            makeMove(Move.move(c0, r0, c1, r1));
        }
    }

    /** Make the MOVE on this Board, assuming it is legal. */
    void makeMove(Move move) {
        if (!legalMove(move)) {
            throw error("Illegal move: %s", move);
        }
        if (move.isPass()) {
            if (_allMoves.get(_allMoves.size() - 1).isPass())  {
                if (numPieces(RED) > numPieces(BLUE)) {
                    _winner = RED;
                } else if (numPieces(BLUE) > numPieces(RED)) {
                    _winner = BLUE;
                } else {
                    _winner = EMPTY;
                }
            }
            pass();
            return;
        }
        _allMoves.add(move);
        _gameStarted = true;
        startUndo();
        PieceColor opponent = _whoseMove.opposite();
        if (move.isJump()) {
            set(move.fromIndex(), EMPTY);
            _numJumps++;
            if (_numJumps >= 25) {
                _winner = EMPTY;
            }
        } else {
            _numJumps = 0;
        }
        set(move.toIndex(), whoseMove());
        ArrayList<Integer> adjacentPieces = findAdjacent(move);
        for (int index : adjacentPieces) {
            if (get(index).isPiece() && get(index) == opponent) {
                set(index, whoseMove());
            }
        }

        if (numPieces(opponent) == 0) {
            _winner = _whoseMove;
        } else if (numPieces(_whoseMove) == 0) {
            _winner = opponent;
        }

        _whoseMove = opponent;
        announce();
    }

    /** Returns array of linearized indices of pieces adjacent to MOVE
     *  toIndex. */
    ArrayList<Integer> findAdjacent(Move move) {
        ArrayList<Integer> returnList = new ArrayList<>();
        int initialIndex = move.toIndex();
        for (int i = -1; i < 2; i++) {
            for (int k = -1; k < 2; k++) {
                if (!(i == 0 && k == 0)) {
                    returnList.add(neighbor(initialIndex, i, k));
                }
            }
        }
        return returnList;
    }

    /** Update to indicate that the current player passes, assuming it
     *  is legal to do so. Passing is undoable. */
    void pass() {
        assert !canMove(_whoseMove);
        _allMoves.add(null);
        startUndo();
        _whoseMove = _whoseMove.opposite();
        announce();
    }

    /** Undo the last move. */
    void undo() {
        Integer currentSquare = _undoSquares.pop();
        PieceColor currentPiece = _undoPieces.pop();
        while (!(currentSquare == null)) {
            unrecordedSet(currentSquare, currentPiece);
            currentSquare = _undoSquares.pop();
            currentPiece = _undoPieces.pop();
        }
        if (_allMoves.get(_allMoves.size() - 1).isJump()) {
            _numJumps--;
        }
        _whoseMove = _whoseMove.opposite();
        _allMoves.remove(_allMoves.size() - 1);
        _winner = null;
        announce();
    }

    /** Indicate beginning of a move in the undo stack. See the
     * _undoSquares and _undoPieces instance variable comments for
     * details on how the beginning of moves are marked. */
    private void startUndo() {
        _undoSquares.add(null);
        _undoPieces.add(null);
    }

    /** Add an undo action for changing SQ on current board. */
    private void addUndo(int sq) {
        _undoSquares.add(sq);
        _undoPieces.add(get(sq));
    }

    /** Return true iff it is legal to place a block at C R. */
    boolean legalBlock(char c, char r) {

        return !_gameStarted && get(c, r) == EMPTY;
    }

    /** Return true iff it is legal to place a block at CR. */
    boolean legalBlock(String cr) {
        return legalBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Set a block on the square C R and its reflections across the middle
     *  row and/or column, if that square is unoccupied and not
     *  in one of the corners. Has no effect if any of the squares is
     *  already occupied by a block.  It is an error to place a block on a
     *  piece. */
    void setBlock(char c, char r) {
        if (!legalBlock(c, r)) {
            throw error("illegal block placement");
        }
        int origin = index('d', '4');
        int blockX = abs('d' - c);
        int blockY = abs('4' - r);
        for (int i = -1; i < 2; i += 2) {
            for (int k = -1; k < 2; k += 2) {
                int target = neighbor(origin, i * blockX, k * blockY);
                if (!(get(target) == BLOCKED)) {
                    set(target, BLOCKED);
                    _totalOpen--;
                }
            }
        }
        if (!canMove(RED) && !canMove(BLUE)) {
            _winner = EMPTY;
        }

        announce();
    }

    /** Place a block at CR. */
    void setBlock(String cr) {
        setBlock(cr.charAt(0), cr.charAt(1));
    }

    /** Return total number of unblocked squares. */
    int totalOpen() {
        return _totalOpen;
    }

    /** Return a list of all moves made since the last clear (or start of
     *  game). */
    List<Move> allMoves() {
        return _allMoves;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Board)) {
            return false;
        }
        Board other = (Board) obj;
        return Arrays.equals(_board, other._board); // FIXME?
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(_board);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        for (char r = '7'; r >= '1'; r -= 1) {
            if (legend) {
                out.format("%c", r);
            }
            out.format(" ");
            for (char c = 'a'; c <= 'g'; c += 1) {
                switch (get(c, r)) {
                case RED:
                    out.format(" r");
                    break;
                case BLUE:
                    out.format(" b");
                    break;
                case BLOCKED:
                    out.format(" X");
                    break;
                case EMPTY:
                    out.format(" -");
                    break;
                default:
                    break;
                }
            }
            out.format("%n");
        }
        if (legend) {
            out.format("   a b c d e f g");
        }
        return out.toString();
    }


    /** Set my notifier to NOTIFY. */
    public void setNotifier(Consumer<Board> notify) {
        _notifier = notify;
        announce();
    }

    /** Take any action that has been set for a change in my state. */
    private void announce() {
        _notifier.accept(this);
    }

    /** A notifier that does nothing. */
    private static final Consumer<Board> NOP = (s) -> { };

    /** Use _notifier.accept(this) to announce changes to this board. */
    private Consumer<Board> _notifier;

    /** For reasons of efficiency in copying the board,
     *  we use a 1D array to represent it, using the usual access
     *  algorithm: row r, column c => index(r, c).
     *
     *  Next, instead of using a 7x7 board, we use an 11x11 board in
     *  which the outer two rows and columns are blocks, and
     *  row 2, column 2 actually represents row 0, column 0
     *  of the real board.  As a result of this trick, there is no
     *  need to special-case being near the edge: we don't move
     *  off the edge because it looks blocked.
     *
     *  Using characters as indices, it follows that if 'a' <= c <= 'g'
     *  and '1' <= r <= '7', then row r, column c of the board corresponds
     *  to _board[(c -'a' + 2) + 11 (r - '1' + 2) ]. */
    private final PieceColor[] _board;

    /** Player that is next to move. */
    private PieceColor _whoseMove;

    /** Number of consecutive non-extending moves since the
     *  last clear or the beginning of the game. */
    private int _numJumps;

    /** Total number of unblocked squares. */
    private int _totalOpen;

    /** Number of blue and red pieces, indexed by the ordinal positions of
     *  enumerals BLUE and RED. */
    private int[] _numPieces = new int[BLUE.ordinal() + 1];

    /** Set to winner when game ends (EMPTY if tie).  Otherwise is null. */
    private PieceColor _winner;

    /** List of all (non-undone) moves since the last clear or beginning of
     *  the game. */
    private ArrayList<Move> _allMoves;

    /* The undo stack. We keep a stack of squares that have changed and
     * their previous contents.  Any given move may involve several such
     * changes, so we mark the start of the changes for each move (including
     * passes) with a null. */

    /** Stack of linearized indices of squares that have been modified and
     *  not undone. Nulls mark the beginnings of full moves. */
    private Stack<Integer> _undoSquares;
    /** Stack of pieces formerly at corresponding squares in _UNDOSQUARES. */
    private Stack<PieceColor> _undoPieces;

    /** Indicates whether game has started. */
    private boolean _gameStarted;

}
