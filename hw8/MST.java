import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/** Minimal spanning tree utility.
 *  @author eduhmc
 */
public class MST {

    /** Given an undirected, weighted, connected graph whose vertices are
     *  numbered 1 to V, and an array E of edges, returns an array of edges
     *  in E that form a minimal spanning tree of the input graph.
     *  Each edge in E is a three-element int array of the form (u, v, w),
     *  where 0 < u < v <= V are vertex numbers, and 0 <= w is the weight
     *  of the edge. The result is an array containing edges from E.
     *  Neither E nor the arrays in it may be modified.  There may be
     *  multiple edges between vertices.  The objects in the returned array
     *  are a subset of those in E (they do not include copies of the
     *  original edges, just the original edges themselves.) */
    public static int[][] mst(int V, int[][] E) {
        interseccion = new UnionFind(V); vacaciones = Arrays.copyOf(E, E.length); contador = new ArrayList<>();
        Arrays.sort(vacaciones, EDGE_WEIGHT_COMPARATOR);
        for (int i = 0; i < vacaciones.length; i++) {
            int v1 = vacaciones[i][0]; int v2 = vacaciones[i][1];
            if (!interseccion.samePartition(v1, v2)) {
                contador.add(vacaciones[i]);
                interseccion.union(v1, v2);
            }
        }
        if (contador == null || contador.size() == 0) {
            return vacaciones;
        } else {
            grafico = new int[contador.size()][];
        }
        for (int i = 0; i < contador.size(); i++) {
            grafico[i] = contador.get(i);
        }
        return grafico;
    }

    /** An ordering of edges by weight. */
    private static final Comparator<int[]> EDGE_WEIGHT_COMPARATOR =
        new Comparator<int[]>() {
            @Override
            public int compare(int[] e0, int[] e1) {
                return e0[2] - e1[2];
            }
        };

    private static int[][] grafico;
    private static ArrayList<int[]> contador;
    private static UnionFind interseccion;
    private static int[][] vacaciones;



}
