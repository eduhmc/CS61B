import java.sql.Array;
import java.util.Arrays;
import java.util.Observable;
/**
 *  @author Josh Hug
 */

public class MazeCycles extends MazeExplorer {
    /* Inherits protected fields:
    protected int[] distTo;
    protected int[] edgeTo;
    protected boolean[] marked;
    */
    private boolean targetFound = false;
    private Maze maze;
    private int[] temp;
    /** Set up to find cycles of M. */
    public MazeCycles(Maze m) {
        super(m);
    }

    @Override
    public void solve() {
        // TODO: Your code here!
        solve_helper(temp, 0);
    }

    // Helper methods go here
    public void solve_helper(int[] i, int s) {
        if (marked[s]) {
            targetFound = true;
            edgeTo[s] = i[s];
            announce();
            int next = i[s];
            while (next != s) {
                edgeTo[next] = i[next];
                announce();
                next = i[next];
            }
        }
        if (targetFound) {
            return;
        }

        marked[s] = true;
        announce();

        for (int w : maze.adj(s)) {
            if (w != i[s]) {
                i[w] = s;
                solve_helper(i, w);
                if (targetFound) {
                    return;
                }
            }
        }

    }
}

