package tablut;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;

import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;
import static tablut.Utils.error;


/** The state of a Tablut Game.
 *  @author Eduardo huerta mercado
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 9;

    /** The throne (or castle) square and its four surrounding squares.. */
    static final Square THRONE = sq(4, 4),
            NTHRONE = sq(4, 5),
            STHRONE = sq(4, 3),
            WTHRONE = sq(3, 4),
            ETHRONE = sq(5, 4);

    /** Initial positions of attackers. */
    static final Square[] INITIAL_ATTACKERS = {
            sq(0, 3), sq(0, 4), sq(0, 5), sq(1, 4),
            sq(8, 3), sq(8, 4), sq(8, 5), sq(7, 4),
            sq(3, 0), sq(4, 0), sq(5, 0), sq(4, 1),
            sq(3, 8), sq(4, 8), sq(5, 8), sq(4, 7)
    };

    /** Initial positions of defenders of the king. */
    static final Square[] INITIAL_DEFENDERS = {
        NTHRONE, ETHRONE, STHRONE, WTHRONE,
            sq(4, 6), sq(4, 2), sq(2, 4), sq(6, 4)
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        init();
        _board = model._board;
        _turn = model._turn;
        _winner = model._winner;
        _repeated = model._repeated;
        _moveCount = model._moveCount;
        _moveLimit = model._moveLimit;
        _pastBoardStates = model._pastBoardStates;
        _previousBoard = model._previousBoard;
    }

    /** Clears the board to the initial position. */
    void init() {
        _board = new Piece[SIZE][SIZE];
        _previousBoard = new Piece[SIZE][SIZE];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                _board[i][j] = EMPTY;
                _previousBoard[i][j] = EMPTY;
            }
        }

        put(KING, kingPosition());

        for (int i = 0; i < INITIAL_DEFENDERS.length; i++) {
            Square currentSquare = INITIAL_DEFENDERS[i];
            put(WHITE, currentSquare);
        }

        for (int i = 0; i < INITIAL_ATTACKERS.length; i++) {
            Square currentSquare = INITIAL_ATTACKERS[i];
            put(BLACK, currentSquare);
        }

        _turn = BLACK;
        _winner = null;
        _repeated = false;
        _moveCount = 0;
        _moveLimit = -1;
        _pastBoardStates = new HashSet<>();


    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     *  /** Return the value of P modulo the size of size.
     *  @param n describing place.
     * */
    void setMoveLimit(int n) {
        if (2 * n < moveCount()) {
            throw error("exceeds move limit");
        }
        _moveLimit = n;
    }

    /** Return a Piece representing whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the winner in the current position, or null if there is no winner
     *  yet. */
    Piece winner() {
        return _winner;
    }

    /** Returns true iff this is a win due to a repeated position. */
    boolean repeatedPosition() {
        return _repeated;
    }

    /** Record current position and set winner() next mover if the current
     *  position is a repeat. */
    private void checkRepeated() {
        if (!_pastBoardStates.contains(encodedBoard())) {
            _pastBoardStates.add(encodedBoard());
        } else {
            _winner = turn().opponent();
            _repeated = true;

        }
    }

    /** Return the number of moves since the initial position that have not been
     *  undone. */
    int moveCount() {
        return _moveCount;
    }

    /** Return location of the king. */
    Square kingPosition() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (_board[i][j] == KING) {
                    return sq(i, j);
                }
            }
        }
        return THRONE;
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        if (col >= 0 && row <= 9) {
            return _board[row][col];

        } else {
            return null;
        }

    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(row - '1', col - 'a');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.row()][s.col()] = p;

    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        _previousBoard = _board;
        _pastBoardStates.add(encodedBoard());
        put(p, s);

    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
        assert from.isRookMove(to);
        if ((from.col() == to.col() && from.row() == to.row())
                || (from.col() != to.col() && from.row() != to.row())) {
            return false;
        } else if (from.col() == to.col() && from.row() != to.row()) {
            if (from.row() > to.row()) {
                int i = from.row();
                while (i > to.row()) {
                    if (get(from.col(), i - 1) != EMPTY) {
                        return  false;
                    }
                    i = i - 1;
                }
            } else {
                int i = from.row();
                while (i < to.row()) {
                    if (get(from.col(), i + 1) != EMPTY) {
                        return  false;
                    }
                    i = i + 1;
                }
            }
        } else if (from.col() != to.col() && from.row() == to.row()) {
            if (from.col() > to.col()) {
                int i = from.col();
                while (i > to.col()) {
                    if (get(i - 1, from.row()) != EMPTY) {
                        return false;
                    }
                    i = i - 1;
                }
            } else {
                int i = from.col();
                while (i < to.col()) {
                    if (get(i + 1, from.row()) != EMPTY) {
                        return  false;
                    }
                    i = i + 1;
                }
            }
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return (get(from) == _turn) || (get(from) == KING && (_turn == WHITE));
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {

        if (isLegal(from)) {
            if ((get(to) == EMPTY)) {
                if (get(from) != KING && to == THRONE) {
                    return false;
                } else {
                    return isUnblockedMove(from, to);
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to());
    }

    /** Move FROM-TO, assuming this is a legal move. */
    void makeMove(Square from, Square to) {
        assert isLegal(from, to);
        revPut(get(from), to);
        put(EMPTY, from);

        Square northSQ = to.rookMove(0, 2);
        Square betweenNorthSQ = to.rookMove(0, 1);
        verifyCapture(northSQ, betweenNorthSQ, to);
        Square eastSQ = to.rookMove(1, 2);
        Square betweenEastSQ = to.rookMove(1, 1);
        verifyCapture(eastSQ, betweenEastSQ, to);
        Square southSQ = to.rookMove(2, 2);
        Square betweenSouthSQ = to.rookMove(2, 1);
        verifyCapture(southSQ, betweenSouthSQ, to);
        Square westSQ = to.rookMove(3, 2);
        Square betweenWestSQ = to.rookMove(3, 1);
        verifyCapture(westSQ, betweenWestSQ, to);

        checkRepeated();

        _moveCount = _moveCount + 1;

        if (legalMoves(_turn).size() == 0) {
            _winner = _turn.opponent();
        }
        if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
        if (_winner != null) {
            return;
        }

        _turn = turn().opponent();
    }
    /** Verify capture.
     * @param northSQ describing place.
     * @param betweenNorthSQ describing place.
     * @param to describing place.
     * */
    void verifyCapture(Square northSQ, Square betweenNorthSQ, Square to) {
        if (northSQ != null) {
            if (get(betweenNorthSQ) != KING) {
                if ((get(to).side() == get(northSQ).side())
                        && (get(betweenNorthSQ) == get(to).opponent())) {
                    capture(to, northSQ);
                }
                if (northSQ == THRONE && get(northSQ) == EMPTY) {
                    capture(to, northSQ);
                }
                if (northSQ == THRONE && get(northSQ) == WHITE) {
                    Integer sumSurroundingThrone = 0;
                    if (get(NTHRONE) == BLACK) {
                        sumSurroundingThrone += 1;
                    }
                    if (get(ETHRONE) == BLACK) {
                        sumSurroundingThrone += 1;
                    }
                    if (get(STHRONE) == BLACK) {
                        sumSurroundingThrone += 1;
                    }
                    if (get(WTHRONE) == BLACK) {
                        sumSurroundingThrone += 1;
                    }
                    if (sumSurroundingThrone > 2 && (to == NTHRONE
                            || to == ETHRONE || to == STHRONE
                            || to == WTHRONE)) {
                        capture(to, northSQ);
                    }
                }
            } else {
                if (betweenNorthSQ == NTHRONE || betweenNorthSQ == ETHRONE
                        || betweenNorthSQ == STHRONE
                        || betweenNorthSQ == WTHRONE) {
                    Square d1 = to.diag1(betweenNorthSQ);
                    Square d2 = to.diag2(betweenNorthSQ);
                    if (get(d1) == BLACK && get(d2) == BLACK
                            && get(THRONE) == EMPTY && get(to) == BLACK) {
                        capture(to, northSQ);
                        _winner = BLACK;
                    }
                } else {
                    if (get(to) == BLACK && betweenNorthSQ != THRONE) {
                        capture(to, northSQ);
                    }
                }

            }
        }
    }
    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        put(EMPTY, sq0.between(sq2));
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _pastBoardStates.remove(encodedBoard());
            _turn = _turn.opponent();
            _moveCount = _moveCount - 1;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (_pastBoardStates.size() == 1) {
            return;

        }
        if (repeatedPosition()) {
            _repeated = true;
            return;
        }
        _board = _previousBoard;
        _repeated = false;



    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                _previousBoard[i][j] = EMPTY;
            }
        }
        _moveCount = 0;

    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        assert side != EMPTY;
        List<Move> lmoves = new ArrayList<Move>();

        HashSet<Square> sideList = pieceLocations(side);
        Piece turnToIgnore = _turn;
        if (side == BLACK) {
            _turn = BLACK;
        } else {
            _turn = WHITE;
        }
        for (Square bsq : sideList) {
            for (Square sqb : SQUARE_LIST) {
                if (isLegal(bsq, sqb)) {
                    lmoves.add(mv(bsq, sqb));
                }
            }
        }
        _turn = turnToIgnore;
        return lmoves;
    }
    /** Return board. */
    public Piece[][] getBoard() {
        return _board;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return legalMoves(side).size() > 0;
    }

    @Override
    public String toString() {
        return toString(true);
    }

    /** Return a text representation of this Board.  If COORDINATES, then row
     *  and column designations are included along the left and bottom sides.
     */
    String toString(boolean coordinates) {
        Formatter out = new Formatter();
        for (int r = SIZE - 1; r >= 0; r -= 1) {
            if (coordinates) {
                out.format("%2d", r + 1);
            } else {
                out.format("  ");
            }
            for (int c = 0; c < SIZE; c += 1) {
                out.format(" %s", get(c, r));
            }
            out.format("%n");
        }
        if (coordinates) {
            out.format("  ");
            for (char c = 'a'; c <= 'i'; c += 1) {
                out.format(" %c", c);
            }
            out.format("%n");
        }
        return out.toString();
    }

    /** Return the locations of all pieces on SIDE. */
    public HashSet<Square> pieceLocations(Piece side) {
        HashSet<Square> hs = new HashSet<>();
        assert side != EMPTY;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (_board[i][j] == side) {
                    hs.add(sq(i, j));
                }
            }
        }
        return hs;
    }

    /** Return the contents of _board in the order of SQUARE_LIST as a sequence
     *  of characters: the toString values of the current turn and Pieces. */
    String encodedBoard() {
        char[] result = new char[Square.SQUARE_LIST.size() + 1];
        result[0] = turn().toString().charAt(0);
        for (Square sq : SQUARE_LIST) {
            result[sq.index() + 1] =
                    get(sq).toString().charAt(0);
        }
        return new String(result);
    }

    /** Piece whose turn it is (WHITE or BLACK). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
    /** Number of (still undone) moves since initial position. */
    private int _moveCount;
    /** True when current board is a repeated position (ending the game). */
    private boolean _repeated;
    /** Board. */
    private Piece[][] _board;
    /** Previous board. */
    private Piece[][] _previousBoard;
    /** All past boards. */
    public HashSet<String> _pastBoardStates = new HashSet<>();
    /** Limit. */
    private int _moveLimit;

}

