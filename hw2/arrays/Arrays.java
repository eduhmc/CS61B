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
        if (A == null) {
            int[][] result = null;
            return result;
        }

        int contador = 1;
        for(int i = 0; i < A.length-1; i++){
            if (A[i] > A[i + 1]){
                contador += 1;
            }
        }

        int[][] result = new int[contador][];

        int w=0, comienzo =0;
        for (int i = 0; i < A.length; i++) {
            if (i < A.length -1 && A[i] >=  A[i + 1]) {
                result[w] = new int[i + 1 - comienzo];
                System.arraycopy(A, comienzo, result[w], 0, i + 1 - comienzo);
                comienzo = i + 1;
                w += 1;
            } else if (i == A.length - 1) {
                result[w] = new int[i + 1 - comienzo];
                System.arraycopy(A, comienzo, result[w], 0, i + 1 - comienzo);
            }
        }

        return result;
    }
}
