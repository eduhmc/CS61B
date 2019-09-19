package image;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *  @author Eduardo Huerta Mercado
 */

public class MatrixUtilsTest {
    /** FIXME
     */
    @Test
    public void testaccumulateVertical() {
        double[][] A = new double[][] {
                {1000000,   1000000,   1000000,   1000000},
                {1000000,     75990,     30003,   1000000},
                {1000000,     30002,    103046,   1000000},
                {1000000,    29515,     38273,   1000000},
                {1000000,     73403,     35399,   1000000},
                {1000000,   1000000,   1000000,   1000000}
        };

        double[][] B = new double[][] {
                {1000000,   1000000,   1000000,   1000000},
                {2000000,  1075990,   1030003,   2000000},
                {2075990,  1060005,   1133049,   2030003},
                {2060005,   1089520,   1098278,   2133049},
                {2089520,   1162923,   1124919,   2098278},
                {2162923,   2124919,   2124919,   2124919}
        };

        double[][] C = MatrixUtils.accumulateVertical(A);
        assertArrayEquals(B, C);
    }

    @Test
    public void testaccumulate() {
        double[][] m1 = new double[][] {
                {1000000,   1000000,   1000000,   1000000},
                {1000000,     75990,     30003,   1000000},
                {1000000,     30002,    103046,   1000000},
                {1000000,    29515,     38273,   1000000},
                {1000000,     73403,     35399,   1000000},
                {1000000,   1000000,   1000000,   1000000}
        };

        double[][] m2 = new double[][] {
                {1000000,   1000000,   1000000,   1000000},
                {2000000,  1075990,   1030003,   2000000},
                {2075990,  1060005,   1133049,   2030003},
                {2060005,   1089520,   1098278,   2133049},
                {2089520,   1162923,   1124919,   2098278},
                {2162923,   2124919,   2124919,   2124919}
        };

        double[][] m3 = new double[][] {
                {1000000,   2000000,   2075990,   2060005},
                {1000000,   1075990,   1060005,   2060005},
                {1000000,  1030002,   1132561,   2060005},
                {1000000,  1029515,   1067788,   2064914},
                {1000000,   1073403,   1064914,   2064914},
                {1000000,   2000000,   2073403,   2064914}
        };

        double[][] X = MatrixUtils.accumulate(m1, MatrixUtils.Orientation.VERTICAL);
        assertArrayEquals(m2, X);

        double[][] Y = MatrixUtils.accumulate(m1, MatrixUtils.Orientation.HORIZONTAL);
        assertArrayEquals(m3, Y);
    }

    @Test
    public void testfindVerticalSeam() {
        double[][] M = new double[][] {
                {1000000,   1000000,   1000000,   1000000},
                {2000000,  1075990,   1030003,   2000000},
                {2075990,  1060005,   1133049,   2030003},
                {2060005,   1089520,   1098278,   2133049},
                {2089520,   1162923,   1124919,   2098278},
                {2162923,   2124919,   2124919,   2124919}
        };
        int[] X = {1, 2, 1, 1, 2, 1};
        int[] Y = MatrixUtils.findVerticalSeam(M);
        assertArrayEquals(X, Y);
    }

    public static void main(String[] args) {

        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
