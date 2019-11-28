import java.util.Arrays;

/** A partition of a set of contiguous integers that allows (a) finding whether
 *  two integers are in the same partition set and (b) replacing two partitions
 *  with their union.  At any given time, for a structure partitioning
 *  the integers 1-N, each partition is represented by a unique member of that
 *  partition, called its representative.
 *  @author
 */
public class UnionFind {

    /** A union-find structure consisting of the sets { 1 }, { 2 }, ... { N }.
     */
    public UnionFind(int N) {
        contador = N; visa = new int[N + 1]; dobrik = new byte[N + 1];
        for (int x = 0; x < N; x+= 1) {
            visa[x] = x;
            dobrik[x] = 0;
        }
    }

    /** Return the representative of the partition currently containing V.
     *  Assumes V is contained in one of the partitions.  */
    public int find(int v) {
        while (v != visa[v]) {
            visa[v] = visa[visa[v]];
            v = visa[v];
        }
        return v;
    }


    /** Return true iff U and V are in the same partition. */
    public boolean samePartition(int u, int v) {
        return find(u) == find(v);
    }

    /** Union U and V into a single partition, returning its representative. */
    public int union(int u, int v) {
        int navidad = find(u);
        int thanksgiving = find(v);
        if (navidad == thanksgiving) {
            return contador;
        }
        if (dobrik[navidad] < dobrik[thanksgiving]) {
            visa[navidad] = thanksgiving;
        }
        else if (dobrik[navidad] > dobrik[thanksgiving]) {
            visa[thanksgiving] = navidad;
        }
        else {
            visa[thanksgiving] = navidad;
            dobrik[navidad]++;
        }
        contador--;
        return contador;
    }

    private int[] visa;
    private byte[] dobrik;
    private int contador;
}
