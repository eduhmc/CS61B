package tablut;

import java.util.HashMap;
import java.util.HashSet;
import static tablut.Piece.*;
import static tablut.Square.*;
import static tablut.Move.mv;
import static tablut.Utils.error;
import java.util.ArrayList;
import java.util.List;
import java.util.Formatter;


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
        this._board = copyArray(model._board);
        _turn = model._turn;
        _moveLimit = Integer.MAX_VALUE;
        _pastBoardStates = model._pastBoardStates;
        _undoStack = model._undoStack;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param a  lol
     * @return array
     *  */
    Piece[][] copyArray(Piece[][] a) {
        Piece[][] nArray = new Piece[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                nArray[i][j] = a[i][j];
            }
        }
        return nArray;
    }
    /** Clears the board to the initial position. */
    void init() {
        _undoStack = new HashMap<>();
        _pastBoardStates = new HashSet<>();
        _turn = BLACK;
        _winner = null;
        _moveLimit = Integer.MAX_VALUE;
        _moveCount = 0;
        _board = new Piece[SIZE][SIZE];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                _board[i][j] = EMPTY;
            }
        }
        _board[THRONE.col()][THRONE.row()] = KING;
        for (int i = 0; i < INITIAL_DEFENDERS.length; i++) {
            Square currentSquare = INITIAL_DEFENDERS[i];
            _board[currentSquare.col()][currentSquare.row()] = WHITE;
        }
        for (int i = 0; i < INITIAL_ATTACKERS.length; i++) {
            Square currentSquare2 = INITIAL_ATTACKERS[i];
            _board[currentSquare2.col()][currentSquare2.row()] = BLACK;
        }
        Piece[][] aux = copyArray(_board);
        _undoStack.put(0, aux);

    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount().
     *  /** Return the value of P modulo the size of size.
     *  @param n describing place.
     * */
    void setMoveLimit(int n) {
        if (2 * n <= moveCount()) {
            throw error("exceeds move limit");
        } else {
            _moveLimit = n;
        }
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
        if (_pastBoardStates.contains(encodedBoard())) {
            _winner = _turn;
        }
        _pastBoardStates.add(encodedBoard());
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
        return _board[col][row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        _board[s.col()][s.row()] = p;
    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        put(p, s);
        _moveCount++;
        captureOpportunity(s);
        Piece[][] aux = copyArray(_board);
        _undoStack.put(_moveCount, aux);
        if (kingPosition().isEdge()) {
            _winner = WHITE;
        }
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param s  lol
     *  */
    void captureOpportunity(Square s) {
        if (s.row() + 2 < 9) {
            Square northSQ = sq(s.col(), s.row() + 2);
            capture(s, northSQ);
        }
        if (s.col() + 2 < 9) {
            Square eastSQ = sq(s.col() + 2, s.row());
            capture(s, eastSQ);
        }
        if (s.row() - 2 >= 0) {
            Square southSQ = sq(s.col(), s.row() - 2);
            capture(s, southSQ);
        }
        if (s.col() - 2 >= 0) {
            Square westSQ = sq(s.col() - 2, s.row());
            capture(s, westSQ);
        }
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param from  lol
     * @param to  lol
     * @param between  lol
     *  */
    void willCaptureP(Square from, Square to, Square between) {
        boolean captured = false;
        boolean isKing = _board[between.col()][between.row()] == KING;
        Piece pFrom = _board[from.col()][from.row()];
        Piece pTo = _board[to.col()][to.row()];
        Piece pBetween = _board[between.col()][between.row()];
        if (pBetween.opponent() == pFrom.side()
                && pBetween.opponent().side() == pTo.side()) {
            captured = true;
        } else if ((pFrom.side() == pBetween.opponent().side()
                && kingPosition() != THRONE && to == THRONE)
                || (pTo.side() == pBetween.opponent().side()
                && kingPosition() != THRONE && from == THRONE)) {
            captured = true;
        } else {
            if (pBetween == WHITE && kingPosition()
                    == THRONE && between.adjacent(THRONE)) {
                int captureVCount = 0;
                for (int i = 0; i < 4; i++) {
                    Square st = INITIAL_DEFENDERS[i];
                    if (_board[st.col()][st.row()] == BLACK) {
                        captureVCount++;
                    }
                }
                if (captureVCount >= 3) {
                    captured = true;
                }
            }
        }
        if (captured) {
            _board[between.col()][between.row()] = EMPTY;
            if (isKing) {
                _winner = BLACK;
            }
        }
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param from  lol
     * @param to lol
     * @param between lol
     *  */
    boolean willCaptureK(Square from, Square to, Square between) {
        int captureVCount = 0;
        if (between == THRONE) {
            for (int i = 0; i < 4; i++) {
                Square st = INITIAL_DEFENDERS[i];
                if (_board[st.col()][st.row()] == BLACK) {
                    captureVCount++;
                }
            }
            if (captureVCount > 3) {
                _board[between.col()][between.row()] = EMPTY;
                _winner = BLACK;
                return true;
            } else {
                return false;
            }
        }
        if (kingPosition() == NTHRONE || kingPosition() == ETHRONE
                || kingPosition() == STHRONE || kingPosition() == WTHRONE) {
            for (int i = 0; i < 4; i++) {
                Square st = INITIAL_DEFENDERS[i];
                if (_board[st.col()][st.row()] == KING) {
                    if (checkDiagonalsKing(st) == 2) {
                        return true;
                    }
                }

            }
        } else {
            willCaptureP(from, to, between);

        }
        return false;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param between lol
     * @return diagonals
     *  */
    int checkDiagonalsKing(Square between) {
        int countDiagonals = 0;
        Square d1 = THRONE.diag1(between);
        Square d2 = THRONE.diag2(between);
        Square d11 = d1.diag1(between);
        Square d22 = d1.diag1(between);
        Piece pd1 = _board[d1.col()][d1.row()];
        Piece pd2 = _board[d2.col()][d2.row()];
        Piece pd11 = _board[d11.col()][d11.row()];
        Piece pd22 = _board[d22.col()][d22.row()];
        if (pd1 == BLACK && pd2 == BLACK) {
            if (pd11 == BLACK || d11 == THRONE) {
                countDiagonals++;
            }
            if (pd22 == BLACK || d11 == THRONE) {
                countDiagonals++;
            }
        }
        return countDiagonals;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, sq(col - 'a', row - '1'));
    }

    /** Return true iff FROM - TO is an unblocked rook move on the current
     *  board.  For this to be true, FROM-TO must be a rook move and the
     *  squares along it, other than FROM, must be empty. */
    boolean isUnblockedMove(Square from, Square to) {
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
        Piece pFrom = _board[from.col()][from.row()];
        put(EMPTY, from);
        revPut(pFrom, to);
        _turn = turn().opponent();
        checkRepeated();

    }
    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        Square between = sq0.between(sq2);
        if (between.adjacent(sq0) && between.adjacent((sq2))) {
            if (_board[between.col()][between.row()] == KING) {
                if (willCaptureK(sq0, sq2, between)) {
                    _board[between.col()][between.row()] = EMPTY;
                    _winner = BLACK;
                }
            } else if (_board[between.col()][between.row()] != KING
                    && _board[between.col()][between.row()] != EMPTY) {
                willCaptureP(sq0, sq2, between);
            }
        }
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            _board = copyArray(_undoStack.get(_moveCount - 1));
            _moveCount = _moveCount - 1;
            _winner = null;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        if (_moveCount > 1) {
            _repeated = false;
        }
    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        if (_undoStack != null) {
            _undoStack.clear();
            _moveCount = 0;
        }


    }

    /** Return a new mutable list of all legal moves on the current board for
     *  SIDE (ignoring whose turn it is at the moment). */
    List<Move> legalMoves(Piece side) {
        assert side != EMPTY;
        List<Move> lmoves = new ArrayList<Move>();
        HashSet<Square> sideList = pieceLocations(side);
        for (Square bsq : sideList) {
            for (Square sqb : SQUARE_LIST) {
                if (isLegalNoTurn(bsq, sqb)) {
                    if (mv(bsq, sqb) != null) {
                        lmoves.add(mv(bsq, sqb));
                    }
                }
            }
        }
        return lmoves;
    }
    /** Return board. */
    public Piece[][] getBoard() {
        return _board;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        return (legalMoves(side).size() > 1);
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

    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param from  lol
     * @param to lol
     *  */
    boolean isLegalNoTurn(Square from, Square to) {
        if (from == to) {
            return false;
        }
        if (isUnblockedMove(from, to)) {
            return true;
        }
        return false;
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
    private HashMap<Integer, Piece[][]> _undoStack;
    /** All past boards. */
    public HashSet<String> _pastBoardStates = new HashSet<>();
    /** Limit. */
    private int _moveLimit;
}

