import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;


/**
 * Test of a BST-based String Set.
 * @author eduhmc
 */
public class ECHashStringSetTest {

    @Test
    public void testlinkedlist() {
        LinkedList<String>[] A = new LinkedList[5];
    }

    @Test
    public void testPut() {
        int N = 1000000;
        ECHashStringSet tester = new ECHashStringSet();
        HashSet prueba = new HashSet();
        String creep = "edu";
        for (int i = 0; i < N; i = i + 1) {
            creep = StringUtils.nextString(creep);
            tester.put(creep);
            prueba.add(creep);
        }
        assertEquals("The size is different", prueba.size(), tester.size());
        boolean veremos = true;
        creep = "edu";
        for (int j = 0; j < N; j += 1) {
            creep = StringUtils.nextString(creep);
            if (!prueba.contains(creep)) {
                veremos = false;
            }
        }
        assertEquals(true, veremos);

        ECHashStringSet tester2 = new ECHashStringSet();
        String edu = "creep";
        HashSet prueba2 = new HashSet();
        for (int i = 0; i < N; i = i + 1) {
            creep = StringUtils.nextString(edu);
            tester2.put(edu);
            prueba2.add(edu);
        }

        boolean veremos2 = true;
        edu = "creep";
        for (int j = 0; j < N; j += 1) {
            edu = StringUtils.nextString(edu);
            if (!prueba.contains(edu)) {
                veremos2 = false;
            }
        }
        assertEquals(false, veremos2);


        ECHashStringSet pruebafinal = new ECHashStringSet();
        pruebafinal.put("Dog");
        pruebafinal.put("Duck");
        pruebafinal.put("Bird");
        pruebafinal.put("Cat");
        assertEquals("size ", 4, 4);
        pruebafinal.put("Human");
        assertEquals("Unique values in the set", 4, 4);
    }


    public static void main(String[] args) {
    }

}