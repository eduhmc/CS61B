package tablut;

import org.junit.Test;
import static org.junit.Assert.*;
import static tablut.Move.mv;

import ucb.junit.textui;
import static tablut.Piece.*;


/** The suite of all JUnit tests for the enigma package.
 *  @author Eduhmc
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test as a placeholder for real ones. */
    //@Test
    //public void dummyTest() {
     //   assertTrue("There are no unit tests!", false);
    //}
    @Test
    public void ultimateTest() {
        Board b = new Board();
        b.makeMove(mv("f9-7"));
        b.makeMove(mv("e6-f"));
        b.makeMove(mv("a6-e"));
        b.makeMove(mv("f5-3"));
        b.makeMove(mv("d9-6"));
        b.makeMove(mv("e5-f"));
        b.makeMove(mv("d1-4"));
        b.makeMove(mv("g5-4"));
        b.makeMove(mv("d6-5"));
        b.makeMove(mv("e3-d"));
        b.makeMove(mv("e2-3"));
        b.makeMove(mv("f3-4"));
        b.makeMove(mv("h5-g"));
        b.makeMove(mv("f6-h"));
        b.makeMove(mv("e3-g"));
        b.makeMove(mv("h6-7"));
        b.makeMove(mv("i6-f"));
        b.makeMove(mv("d3-c"));
        b.makeMove(mv("e1-4"));
        b.makeMove(mv("c3-f"));
        b.makeMove(mv("i4-g"));
        b.makeMove(mv("f3-4"));
        b.makeMove(mv("i5-h"));
        b.makeMove(mv("h7-8"));
        b.makeMove(mv("g4-h"));
        b.makeMove(mv("h8-i"));
        b.makeMove(mv("h4-g"));
        b.makeMove(mv("f5-4"));
        b.makeMove(mv("g4-h"));
        b.makeMove(mv("f4-5"));
        assertTrue(b.winner() == null);
        b.makeMove(mv("h4-f"));
        assertTrue(b.winner() == BLACK);
    }

}


