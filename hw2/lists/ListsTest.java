package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author Eduardo Huerta Mercado
 */

public class ListsTest {
    /** FIXME
     */
    @Test
    public void testnaturalRuns() {
    IntList A = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
    IntList A1 = IntList.list(1, 3, 7);
    IntList A2 = IntList.list(5);
    IntList A3 = IntList.list(4, 6, 9, 10);
    IntList A4 = IntList.list(10, 11);
    IntListList B = IntListList.list(A1, A2, A3, A4);
    IntListList C = Lists.naturalRuns(A);
    assertEquals(B, C);

    IntList D = IntList.list();
    assertEquals(null, Lists.naturalRuns(D));
}

    // It might initially seem daunting to try to set up
    // IntListList expected.
    //
    // There is an easy way to get the IntListList that you want in just
    // few lines of code! Make note of the IntListList.list method that
    // takes as input a 2D array.

    public static void main(String[] args) {

    System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
