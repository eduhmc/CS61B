package lists;

/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

/** List problem.
 *  @author Eduardo Huerta-Mercado
 */
class Lists {
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntListList naturalRuns(IntList L) {
        if (L == null){
            return null;
        }
        IntList ahora = L;
        IntList doble_lista = L;
        IntListList solucion = new IntListList();
        while (ahora != null){
            if (ahora.tail == null){
                ahora = ahora.tail;
                doble_lista.tail = null;
                break;
            }
        else if (ahora.tail.head > ahora.head) {
            ahora = ahora.tail;
            doble_lista = doble_lista.tail;
        } else {
            ahora = ahora.tail;
            doble_lista.tail = null;
            break;
        }
    }

    solucion = new IntListList(L, naturalRuns(ahora));
        return solucion;
    }
}

