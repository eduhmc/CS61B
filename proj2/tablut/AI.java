package tablut;


import java.util.HashSet;

import static java.lang.Math.*;

import static tablut.Square.sq;
import static tablut.Piece.*;

/** A Player that automatically generates moves.
 *  @author eduhmc
 */
class AI extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A position-score magnitude indicating a forced win in a subsequent
     *  move.  This differs from WINNING_VALUE to avoid putting off wins. */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        _lastFoundMove = null;
        findMove(b, maxDepth(b), true,
                (_myPiece == WHITE ? 1 : -1), -INFTY, INFTY);
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        int moveValue = 0;
        if (sense == -1) {
            if (depth > 0) {
                moveValue = minimalMove(board, saveMove, alpha, beta);
            } else {
                if (board.winner() == BLACK) {
                    return WINNING_VALUE * (-1);
                } else if (board.winner() == WHITE) {
                    return WINNING_VALUE;
                } else {
                    if (depth == 0) {
                        return staticScore(board);
                    } else {
                        return 0;
                    }
                }
            }
        }
        if (sense == 1) {
            if (depth > 0) {
                moveValue = maximalMove(board, saveMove, alpha, beta);
            } else {
                if (board.winner() == BLACK) {
                    return WINNING_VALUE * (-1);
                } else if (board.winner() == WHITE) {
                    return WINNING_VALUE;
                } else {
                    if (depth == 0) {
                        return staticScore(board);
                    } else {
                        return 0;
                    }
                }
            }

        }
        if (saveMove) {
            _lastFoundMove = null;
        }
        return moveValue;
    }
    /** A magnitude greater than a normal value.
     * @param board place
     * @param saveMove describing sizes
     * @param a describing sizes
     * @param b describing sizes
     * @return the size of cycle
     * */
    public int minimalMove(Board board, boolean saveMove, int a, int b) {
        int minimalValue = INFTY;
        for (Move lmove: board.legalMoves(BLACK)) {
            board.makeMove(lmove);
            int possibleMax = maximalMove(board, false, a, b);
            board.undo();
            board._pastBoardStates.clear();
            if (minimalValue >= possibleMax) {
                minimalValue = possibleMax;
                a = min(a, possibleMax);
                if (saveMove) {
                    _lastFoundMove = lmove;
                }
                if (a >= b) {
                    board.undo();
                    break;
                }
            }
        }
        return minimalValue;
    }
    /** A magnitude greater than a normal value.
     * @param board place
     * @param saveMove describing sizes
     * @param a describing sizes
     * @param b describing sizes
     * @return the size of cycle
     * */
    public int maximalMove(Board board, boolean saveMove, int a, int b) {
        int maximalValue = -INFTY;
        for (Move lmove: board.legalMoves(WHITE)) {
            board.makeMove(lmove);
            int possibleMin = minimalMove(board, false, a, b);
            board.undo();
            board._pastBoardStates.clear();
            if (maximalValue <= possibleMin) {
                maximalValue = possibleMin;
                a = min(a, possibleMin);
                if (saveMove) {
                    _lastFoundMove = lmove;
                }
                if (a >= b) {
                    board.undo();
                    break;
                }
            }
        }
        return maximalValue;
    }
    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private static int maxDepth(Board board) {
        Piece currentTurn = board.turn();
        int maxDepthValue = 3;
        if (board.pieceLocations(WHITE).size() < 4
                && board.pieceLocations(BLACK).size() <= 5) {
            return 3;
        }
        if (board.pieceLocations(WHITE).size() > 6
                && board.pieceLocations(BLACK).size() > 12) {
            return 2;
        }
        return 3;
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int heuristicValue = 0;
        Board testBoard = new Board();
        testBoard.copy(board);
        Piece currentTurn = testBoard.turn();
        if (currentTurn == WHITE) {
            heuristicValue = heuristicValue
                    + (heuristicBlackWhite(board, testBoard) * -3000);
        } else {
            heuristicValue = heuristicValue
                    + (heuristicBlackWhite(board, testBoard) * 1500);
        }
        heuristicValue = heuristicValue + heuristicKing(getPiecesBoard(board),
                new HashSet<>(), 1024, 5, board.kingPosition());

        return heuristicValue;
    }
    /** A magnitude greater than a normal value.
     * @param board place
     * @param testBoard describing sizes
     * @return the size of cycle
     * */
    public  int heuristicBlackWhite(Board board, Board testBoard) {
        Piece currentTurn = testBoard.turn();
        int legalMovesCounter = 0;
        for (Move lmove : testBoard.legalMoves(currentTurn)) {
            testBoard.makeMove(lmove);
            if (testBoard.repeatedPosition()) {
                legalMovesCounter = legalMovesCounter + 0;
            } else {
                legalMovesCounter = legalMovesCounter
                        + board.pieceLocations(currentTurn).size()
                        - testBoard.pieceLocations(currentTurn).size();
            }
            testBoard = new Board();
            testBoard.copy(board);
        }
        return legalMovesCounter;
    }
    /** A magnitude greater than a normal value.
     * @param board place
     * @param kingPath describing sizes
     * @param verifyingNumber describing sizes
     * @param dir describing sizes
     * @param kingPos describing sizes
     * @return the size of cycle
     * */
    public int heuristicKing(Piece[][] board, HashSet<Square> kingPath,
                              int verifyingNumber, int dir, Square kingPos) {
        int heuristicKingValue = 0;
        if (kingPath.contains(kingPos) || verifyingNumber < 3) {
            return heuristicKingValue;
        } else {
            kingPath.add(kingPos);
            Piece[][] testBoard = copyArray(board);
            for (int currentDir = 0; currentDir < 4; currentDir++) {
                if (!verifyEdge(testBoard, currentDir, kingPos)) {
                    int testCol = kingPos.col()
                            + possibleCols[currentDir];
                    int testRow = kingPos.row()
                            + possibleRows[currentDir];
                    boolean rowBoolean = testRow >= 0
                            && testRow < testBoard.length;
                    boolean colBoolean = testCol >= 0
                            && testCol < testBoard[0].length;
                    if (colBoolean && rowBoolean
                            && testBoard[testRow][testCol] == EMPTY) {
                        testBoard[kingPos.row()][kingPos.col()] = EMPTY;
                        testBoard[testRow][testCol] = KING;
                        heuristicKingValue += heuristicKing(testBoard, kingPath,
                                (currentDir == dir ? verifyingNumber
                                        : verifyingNumber / 2),
                                currentDir, sq(testCol, testRow));
                        testBoard = copyArray(board);
                    }
                } else {
                    heuristicKingValue += verifyingNumber;
                }
            }
            return heuristicKingValue;
        }
    }
    /** A magnitude greater than a normal value.
     * @param array describing sizes
     * @return Array.
     * */

    public Piece[][] copyArray(Piece[][] array) {
        Piece[][] newArray = new Piece[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                newArray[i][j] = array[i][j];
            }
        }
        return newArray;
    }
    /** Return the value of P modulo the size of size.
     * @param copiedBoard describing place
     * @param currentDir describing sizes
     * @param kingPos describing sizes
     * @return the size of cycle
     */
    public boolean verifyEdge(Piece[][] copiedBoard,
                              int currentDir, Square kingPos) {
        int testRow = kingPos.row() + possibleRows[currentDir];
        int testCol = kingPos.col() + possibleCols[currentDir];
        boolean verifyEdgeValue = false;
        boolean rowBoolean = testRow >= 0 && testRow < copiedBoard.length;
        boolean colBoolean = testCol >= 0 && testCol < copiedBoard[0].length;
        Piece[][] testBoard2 = copiedBoard;
        while (rowBoolean && colBoolean) {
            if (testBoard2[testRow][testCol] == WHITE
                    || testBoard2[testRow][testCol] == BLACK) {
                testRow = testRow + possibleRows[currentDir];
                testCol = testCol + possibleCols[currentDir];
            } else {
                return verifyEdgeValue;
            }
        }
        verifyEdgeValue = true;
        return verifyEdgeValue;
    }
    /** A magnitude greater than a normal value. */
    private int[] possibleRows = new int[]{-1, 0, 1, 0};
    /** A magnitude greater than a normal value. */
    private int[] possibleCols = new int[]{0, 1, 0, -1};
    /** A magnitude greater than a normal value.
     * @param board describing sizes
     * @return board.
     * */
    public Piece[][] getPiecesBoard(Board board) {
        Piece[][] testBoard
                = new Piece[board.getBoard().length][board.getBoard().length];
        for (int i = 0; i < board.getBoard().length; i += 1) {
            for (int j = 0; j < board.getBoard().length; j += 1) {
                testBoard[i][j] = board.getBoard()[i][j];
            }
        }
        return testBoard;
    }

}
