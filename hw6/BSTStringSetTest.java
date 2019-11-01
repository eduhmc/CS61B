import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author eduhmc
 */
public class BSTStringSetTest  {
    // FIXME: Add your own tests for your BST StringSet

    @Test
    public void trying(){
        BSTStringSet shit = new BSTStringSet();
        shit.put("Nick");
        assertEquals(shit.contains("Nick"), true);

        BSTStringSet nico = new BSTStringSet();
        nico.put("Huati");
        assertEquals(nico.contains("Huati"), true);

        BSTStringSet dazo = new BSTStringSet();
        dazo.put("Tipo");
        assertEquals(dazo.contains("chino"), false);





    }
    @Test
    public  void BSTStringSet(){

    }
    public void testNothing() {
        // FIXME: Delete this function and add your own tests
    }
}
