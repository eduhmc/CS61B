package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author Eduardo Huerta-Mercado
 */

public class ArraysTest {
    /** FIXME
     */
    @Test
    public void testcatenate() {
        int[] x = {1, 3, 5};
        int[] y = {2, 4, 6};
        int[] z = {1, 3, 5, 2, 4, 6};
        assertArrayEquals(z, Arrays.catenate(x, y));
        assertArrayEquals(y, Arrays.catenate(null, y));
        assertArrayEquals(x, Arrays.catenate(x,null));
    }

    @Test
    public void testremove() {
        int[] A = {1, 5, 7, 9, 10, 2, 8, 7};
        int[] B = Arrays.remove(A, 2, 3);
        int[] C = {1, 5, 2, 8, 7};
        int[] D = {9, 10, 2, 8, 7};
        int[] E = {1, 5, 7, 9, 10, 2, 8};
        int[] X = {10, 9, 8, 7, 6, 5};
        int[] Y = {10, 6, 5};
        assertArrayEquals(Y, Arrays.remove(X, 1, 3));
        assertArrayEquals(C, B);
        assertArrayEquals(D, Arrays.remove(A, 0, 3));
        assertArrayEquals(A, Arrays.remove(A, 5, 0));
        assertArrayEquals(E, Arrays.remove(A, 7, 1));
    }

    @Test
    public void testnaturalRuns() {
        int[] A = {1, 2, 6, 5, 4, 6, 10, 11};
        int[][] B = {{1, 2, 6}, {5}, {4, 6, 10, 11}};
        int[] C = {1, 2, 3};
        int[][] D = {C};
        assertArrayEquals(B, Arrays.naturalRuns(A));
        assertArrayEquals(D, Arrays.naturalRuns(C));
    }











    public static void main(String[] args) {

        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
