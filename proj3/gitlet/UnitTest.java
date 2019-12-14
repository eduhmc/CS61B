package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Eduhmc
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
        int edu = 3;
        assertEquals(3, edu);
    }
    /** TEST */
    public boolean deleteDirectory(File x) {
        if (!x.exists()) {
            System.out.println("all good");
            return false;
        }
        File[] lst = x.listFiles();
        if (lst != null) {
            for (File file : lst) {
                deleteDirectory(file);
            }
        }
        return x.delete();
    }

    boolean deleteDirectory(String fileName) {
        File theFile = new File(fileName);
        return deleteDirectory(theFile);
    }
}


