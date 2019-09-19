package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

/** Array utilities.
 *  @author Eduardo Huerta Mercado
 */
class Arrays {
    /* C. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        /* *Replace this body with the solution. */
        if (A == null) {
            return B;
        }
        if (B == null) {
            return A;
        }
        int[] resultado = new int[A.length + B.length];
        System.arraycopy(A, 0, resultado, 0, A.length);
        System.arraycopy(B, 0, resultado, A.length , B.length);
        return resultado;
    }

    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        /* *Replace this body with the solution. */
        int[] resultado = new int[A.length-len];
        System.arraycopy(A, 0, resultado, 0, start );
        System.arraycopy(A, start+len, resultado, start, A.length-(start+len));
        return resultado;
    }

    /* E. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        /* *Replace this body with the solution. */
        int count = 1;
        for (int i = 0; i < A.length - 1; i++) {
            if(A[i] >= A[i + 1]) {
                count++;
            }
        }
        int[][] result = new int[count][];
        int index = 0;
        int k = 0;
        int len = 1;
        for (int i = 0; i < A.length - 1; i++) {
            if (A[i] >= A[i+1]) {
                result[index] = Utils.subarray(A, k, len);
                len = 1;
                k = i+1;
                index++;
            }
            else {
                len ++;
            }
        }
        result[index] = Utils.subarray(A, k, len);
        return result;
    }
}