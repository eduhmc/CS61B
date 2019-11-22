import java.util.LinkedList;
import java.util.Observable;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *  @author Josh Hug
 */
public class MazeBreadthFirstPaths extends MazeExplorer {
    /* Inherits visible fields:
    protected int[] distTo;
    protected int[] edgeTo;
    protected boolean[] marked;
    */
    private int s;
    private int t;
    private boolean targetFound = false;
    private Maze maze;


    /** A breadth-first search of paths in M from (SOURCEX, SOURCEY) to
     *  (TARGETX, TARGETY). */
    public MazeBreadthFirstPaths(Maze m, int sourceX, int sourceY,
                                 int targetX, int targetY) {
        super(m);
        // Add more variables here!
        maze = m;
        s = maze.xyTo1D(sourceX, sourceY);
        t = maze.xyTo1D(targetX, targetY);
        distTo[s] = 0;
        edgeTo[s] = s;
    }

    /** Conducts a breadth first search of the maze starting at the source. */
    private void bfs(int v) {
        // TODO: Your code here. Don't forget to update distTo, edgeTo,
        // and marked, as well as call announce()
        marked[v] = true;
        announce();

        Queue<Integer> q = new LinkedList<>();
        q.add(v);

        while (!q.isEmpty()) {
            int i = q.remove();
            for (int w: maze.adj(i)) {
                if (i == t) { targetFound = true; }
                if (targetFound) { return; }
                if (!marked[w]) {
                    edgeTo[w] = i;
                    announce();
                    distTo[w] = distTo[i] + 1;
                    announce();
                    marked[w] = true;
                    announce();
                    q.add(w);
                }
            }
        }
    }


    @Override
    public void solve() {
        bfs(s);
    }
}

