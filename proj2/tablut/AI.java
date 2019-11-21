package tablut;


import java.util.ArrayList;
import static java.lang.Math.*;

import static tablut.Square.sq;
import static tablut.Piece.*;


/** A Player that automatically generates moves.
 *  @author eduhmc
 */
class AI extends Player {

    /**
     * A position-score magnitude indicating a win (for white if positive,
     * black if negative).
     */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /**
     * A position-score magnitude indicating a forced win in a subsequent
     * move.  This differs from WINNING_VALUE to avoid putting off wins.
     */
    private static final int WILL_WIN_VALUE = Integer.MAX_VALUE - 40;
    /**
     * An integer so style check doesn't say magic number.
     */
    private static final int INFTY = Integer.MAX_VALUE;


    /**
     * An integer so style check doesn't say magic number.
     */
    private static final int HEURISTIC_SIDE_WEIGHT = 1;
    /**
     * A magnitude greater than a normal value.
     */
    private static final int HEURISTIC_SIDES_WEIGHT = HEURISTIC_SIDE_WEIGHT
            * HEURISTIC_SIDE_WEIGHT * HEURISTIC_SIDE_WEIGHT
            * HEURISTIC_SIDE_WEIGHT;
    /**
     *  An integer so style check doesn't say magic number.
     */
    private static final int NUMBER_B_PIECES = 16;
    /**
     * An integer so style check doesn't say magic number.
     */
    private static final int NUMBER_W_PIECES = 9;
    /**
     * An integer so style check doesn't say magic number.
     */
    private static final int NUMBER_DIRECTIONS = 4;
    /**
     * An integer so style check doesn't say magic number.
     */
    private static final int SIZE = 9;
    /**
     * A new AI with no piece or controller (intended to produce
     * a template).
     */
    AI() {
        this(null, null);
    }

    /**
     * A new AI playing PIECE under control of CONTROLLER.
     */
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

    /**
     * Return a move for me from the current position, assuming there
     * is a move.
     */
    private Move findMove() {
        Board b = _controller.board();
        int sense = _myPiece == WHITE ? 1 : -1;
        findMove(b, maxDepth(b), true, sense, -INFTY, INFTY);
        return _lastFoundMove;
    }

    /**
     * The move found by the last call to one of the ...FindMove methods
     * below.
     */
    private Move _lastFoundMove;

    /**
     * Find a move from position BOARD and return its value, recording
     * the move found in _lastFoundMove iff SAVEMOVE. The move
     * should have maximal value or have value > BETA if SENSE==1,
     * and minimal value or value < ALPHA if SENSE==-1. Searches up to
     * DEPTH levels.  Searching at level 0 simply returns a static estimate
     * of the board value and does not set _lastMoveFound.
     */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {
        int moveValue = extremeMove(sense, board, depth, saveMove, alpha, beta);
        return moveValue;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param sense  lol
     * @param board lol
     * @param depth lol
     * @param save  lol
     * @param a lol
     * @param b lol
     * @return value
     *  */
    public int extremeMove(int sense, Board board,
                           int depth, boolean save, int a, int b) {
        if (board.winner() == WHITE || board.winner() == BLACK) {
            return staticScore(board);
        }
        if (depth == 0) {
            return staticScore(board);
        }
        int extremeValue = 0;
        if (sense == 1) {
            extremeValue = -INFTY;
            for (Move lmove : board.legalMoves(WHITE)) {
                board.makeMove(lmove);
                int possibleMax = extremeMove(-1,
                        board, depth - 1, false, a, b);
                board.undo();
                board.clearing();
                if (possibleMax >= extremeValue) {
                    extremeValue = possibleMax;
                    a = max(a, possibleMax);
                    if (save) {
                        _lastFoundMove = lmove;
                    }
                    if (b <= a) {
                        board.undo();
                        break;
                    }
                }
            }
        } else if (sense == -1) {
            extremeValue = INFTY;
            for (Move lmove : board.legalMoves(BLACK)) {
                board.makeMove(lmove);
                int possibleMin = extremeMove(1, board, depth - 1, false, a, b);
                board.undo();
                board.clearing();
                if (possibleMin <= extremeValue) {
                    extremeValue = possibleMin;
                    b = min(b, possibleMin);
                    if (save) {
                        _lastFoundMove = lmove;
                    }
                    if (a >= b) {
                        board.undo();
                        break;
                    }
                }
            }

        }
        return extremeValue;
    }

    /**
     * Return a heuristically determined maximum search depth
     * based on characteristics of BOARD.
     */
    private static int maxDepth(Board board) {
        return 1;
    }

    /**
     * Return a heuristic value for BOARD.
     */
    private int staticScore(Board board) {

        if (board.winner() == WHITE || board.winner() == BLACK) {
            return (board.winner() == WHITE ? WINNING_VALUE : -WINNING_VALUE);
        }
        if (board.kingPosition() == null) {
            return -WINNING_VALUE;
        }
        int score = 0;

        if (board.kingPosition().isEdge()) {
            score = WINNING_VALUE;
            return score;
        }
        score = countKingHeuristic(board.kingPosition())
                * HEURISTIC_SIDES_WEIGHT;

        ArrayList<Square> sideWList = new ArrayList<>();
        int heuristicW = countSideHeuristic(WHITE, board, sideWList);
        double distanceW = countHeuristicDistance(sideWList, board);

        ArrayList<Square> sideBList = new ArrayList<>();
        int  heuristicB = countSideHeuristic(BLACK, board, sideWList);
        double distanceB = countHeuristicDistance(sideBList, board);

        score = score + (heuristicW - heuristicB);

        score = score + (int) (distanceB - distanceW) * HEURISTIC_SIDE_WEIGHT;

        return score;

    }

    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param kingPosition  position of king
     * @return value
     *  */
    public int countKingHeuristic(Square kingPosition) {
        int kingHeuristic = 0;
        int kc = kingPosition.col();
        int kr = kingPosition.row();

        if (board().isLegalNoTurn(kingPosition, sq(kc, 8))) {
            kingHeuristic++;
        }
        if (board().isLegalNoTurn(kingPosition, sq(kc, 0))) {
            kingHeuristic++;
        }
        if (board().isLegalNoTurn(kingPosition, sq(0, kr))) {
            kingHeuristic++;
        }
        if (board().isLegalNoTurn(kingPosition, sq(8, kr))) {
            kingHeuristic++;
        }

        return kingHeuristic;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param side defines side
     * @param board is board
     * @param sideList which side is it
     * @return value
     *  */
    public int countSideHeuristic(Piece side, Board board,
                                  ArrayList<Square> sideList) {
        int sideCounter = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.get(i, j).side() == side) {
                    sideList.add(sq(i, j));
                    sideCounter++;
                }
            }
        }
        if (side == BLACK) {
            sideCounter = sideCounter * ((NUMBER_DIRECTIONS
                    * HEURISTIC_SIDE_WEIGHT * HEURISTIC_SIDE_WEIGHT)
                    / NUMBER_B_PIECES);
        } else {
            sideCounter = sideCounter * ((NUMBER_DIRECTIONS
                    * HEURISTIC_SIDE_WEIGHT * HEURISTIC_SIDE_WEIGHT)
                    / NUMBER_W_PIECES);
        }
        return sideCounter;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param sideList  arraylist
     * @param board board
     * @return value
     *  */
    public double countHeuristicDistance(ArrayList<Square> sideList,
                                         Board board) {
        double sideDistance = 0;
        for (Square x : sideList) {
            sideDistance = sideDistance
                    + simulateDistance(board.kingPosition(), x);

        }
        return sideDistance;
    }
    /** Return true iff MOVE is a legal move in the current
     *  position.
     * @param sq0 x direc
     * @param sq1 x2 direc
     * @return value
     *  */
    public Double simulateDistance(Square sq0, Square sq1) {
        return Math.sqrt(Math.pow((sq0.col() - sq1.col()), 2)
                + Math.pow((sq0.row() - sq1.row()), 2));
    }

}
