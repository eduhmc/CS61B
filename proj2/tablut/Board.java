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
        // FIXME -edited
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
        // FIXME - edited
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
            put(WHITE,currentSquare);
        }

        for (int i = 0; i < INITIAL_ATTACKERS.length; i++) {
            Square currentSquare = INITIAL_ATTACKERS[i];
            put(BLACK,currentSquare);
        }

        _turn = BLACK;
        _winner = EMPTY;
        _repeated = false;
        _moveCount = 0;
        _moveLimit = 9999;
        _pastBoardStates = null;


    }

    /** Set the move limit to LIM.  It is an error if 2*LIM <= moveCount(). */
    void setMoveLimit(int n) {
        // FIXME - Edited
        _moveLimit = n;

        if (2 * _moveLimit < moveCount()) {
            throw error("exceeds move limit");
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
        // FIXME - edited
        if ( ! _pastBoardStates.contains( encodedBoard() ) ) {
            _pastBoardStates.add(encodedBoard());
        }
        else {
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
                if (_board[i][j] == KING){
                    return sq(i,j);
                };

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
        // FIXME - edited

        if (col >= 0 && row <= 9) {
            return _board[col][row];

        } else {
            return null;
        }

    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(row - '1', col - 'a'); // get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        // FIXME  - edited
        _board[s.col()][s.row()] = p;

    }

    /** Set square S to P and record for undoing. */
    final void revPut(Piece p, Square s) {
        // FIXME - edited
        _previousBoard = _board;
        _pastBoardStates.add(encodedBoard()); // state before last change
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
        // FIXME - edited
        if (from.isRookMove(to)) {
            //check squares along - up
            if (from.row() + 1 <= 9) {
                if (get(from.col(), from.row() + 1) != EMPTY){
                    return false;
                }
            }
            //check squares along - down
            if (from.row() - 1 >= 0) {
                if (get(from.col(), from.row() - 1) != EMPTY){
                    return false;
                }
            }
            //check squares along - right
            if (from.col() + 1 <= 9) {
                if (get(from.col() + 1, from.row()) != EMPTY){
                    return false;
                }
            }
            //check squares along - left
            if (from.col() - 1 >= 0) {
                if (get(from.col() - 1, from.row()) != EMPTY){
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }

    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from) == _turn;
    }

    /** Return true iff FROM-TO is a valid move. */
    boolean isLegal(Square from, Square to) {
        // FIXME - edited
        if (isLegal(from) && isUnblockedMove(from, to) && to != THRONE ) {
            return true;
        }
        else {
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
        // FIXME
        if (moveCount() + 1 <= _moveLimit) {
            revPut(get(from), to);
            put(EMPTY, from);
            _moveCount = _moveCount + 1;
            _turn = turn().opponent();
        }

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to());
    }

    /** Capture the piece between SQ0 and SQ2, assuming a piece just moved to
     *  SQ0 and the necessary conditions are satisfied. */
    private void capture(Square sq0, Square sq2) {
        // FIXME - Edited
        //make sq1 EMPTY
        put(EMPTY, sq0.between(sq2));
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (_moveCount > 0) {
            undoPosition();
            // FIXME
            _pastBoardStates.remove(encodedBoard());
            _turn = _turn.opponent();
            _moveCount = _moveCount - 1;
        }
    }

    /** Remove record of current position in the set of positions encountered,
     *  unless it is a repeated position or we are at the first move. */
    private void undoPosition() {
        // FIXME

        if(_pastBoardStates.size() == 1) {
            return;

        }
        if(repeatedPosition()) {
            _repeated = true;
            return;
        }
        _board = _previousBoard;
        _repeated = false;



    }

    /** Clear the undo stack and board-position counts. Does not modify the
     *  current position or win status. */
    void clearUndo() {
        // FIXME - Edited
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
        // FIXME - Edited
        List<Move> l_Moves = new ArrayList<Move>();

        HashSet<Square> sideList = pieceLocations(side);

        for (Square bsq : sideList) {
            int r0 = bsq.row(), c0 = bsq.col(), i0 = bsq.index();
            for (int d = 0; d < 4; d += 1) {
                for (Square sq1 : ROOK_SQUARES[i0][d]) {
                    l_Moves.add(mv(bsq, sq1));
                }
            }
        }

        return l_Moves;
    }

    /** Return true iff SIDE has a legal move. */
    boolean hasMove(Piece side) {
        // FIXME

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
    private HashSet<Square> pieceLocations(Piece side) {
        // FIXME - edited
        HashSet<Square> hs = new HashSet<>();
        assert side != EMPTY;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (_board[i][j] == side) {
                    hs.add(sq(i,j));
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
            result[sq.index() + 1] = get(sq).toString().charAt(0); //W or B or K or -
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

    // FIXME: Other state?
    private Piece[][] _board;
    private Piece[][] _previousBoard;
    private HashSet<String> _pastBoardStates = new HashSet<>();
    private int _moveLimit;

}
