package enigma;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.LinkedList;
import static enigma.TestUtils.*;

public class MyTests {
    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors,
                          String notches, Rotor rotor) {
        rotor = new MovingRotor(name,
                new Permutation(rotors.get(name), UPPER),
                notches);
    }
    public static void main(String[] args) {
        new MyTests().testPermutations();
        new MyTests().testMachine();
        new MyTests().testInverse();
    }



    @Test
    public void testPermutations() {

        Permutation p = new Permutation(
                "(EDU) (ARFOHAXTMCW) (JO)", new Alphabet());
        assertEquals(p.permute('A'), 'R');
        assertEquals(p.permute('E'), 'D');
        assertEquals(p.permute('U'), 'E');
        assertEquals(p.permute('X'), 'T');
        assertEquals(p.permute('W'), 'A');

        Permutation pnew = new Permutation(
                "(ZYX) (MNOPLQ) (J)", new Alphabet());
        assertEquals(pnew.permute('J'), 'J');
        assertEquals(pnew.permute('Z'), 'Y');
        assertEquals(pnew.permute('X'), 'Z');
        assertEquals(pnew.permute('A'), 'A');
        assertEquals(pnew.permute('O'), 'P');
    }


    @Test
    public void testInverse() {


        Permutation p = new Permutation(
                "(EFG) (ABCFIKLZYXW) (PC)", new Alphabet());
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.permute('E'), 'F');
        assertEquals(p.invert('I'), 'F');
        assertEquals(p.invert('A'), 'W');
        assertEquals(p.invert('P'), 'C');

        Permutation newp = new Permutation(
                "(ABC) (DEFGHI) (KL) (O)", new Alphabet());
        assertEquals(newp.invert('B'), 'A');
        assertEquals(newp.invert('F'), 'E');
        assertEquals(newp.invert('O'), 'O');
        assertEquals(newp.invert('X'), 'X');
        assertEquals(newp.invert('G'), 'F');
    }


    @Test
    public void testMachine() {
        LinkedList<Rotor> allRotors = new LinkedList<>();

        allRotors.add(movingRotorI);
        allRotors.add(movingRotorII);
        allRotors.add(movingRotorIII);
        allRotors.add(movingRotorIV);
        allRotors.add(movingRotorV);
        allRotors.add(movingRotorVI);
        allRotors.add(movingRotorVII);
        allRotors.add(movingRotorVIII);
        allRotors.add(fixedRotorBeta);
        allRotors.add(fixedRotorGamma);
        allRotors.add(reflectorB);
        allRotors.add(reflectorC);

        Machine myMachine = new Machine(currentAlphabet, 5, 3, allRotors);
        String[] insertedRotors = new String[] {reflectorB.toString(),
                fixedRotorBeta.toString(),
                movingRotorIII.toString(), movingRotorIV.toString(),
                movingRotorI.toString()};
        myMachine.insertRotors(insertedRotors);
        myMachine.setRotors("AXLE");
        myMachine.setPlugboard(new Permutation("(YF) (ZH)", currentAlphabet));
        String tempChar = myMachine.convert("Y");


        for (int i = 0; i < 11; i += 1) {
            myMachine.convert("A");
        }

        myMachine.convert("A");


        for (int i = 0; i < 597; i += 1) {
            myMachine.convert("A");
        }
        myMachine.convert("A");
        myMachine.convert("A");
    }


    /** Rotors */
    static Alphabet currentAlphabet = new Alphabet();
    static MovingRotor movingRotorI = new MovingRotor("I",
            new Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
                    + "(DFG) (IV) (JZ) (S)", currentAlphabet), "Q");
    static MovingRotor movingRotorII = new MovingRotor("II",
            new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) "
                    + "(GR) (NT) (A) (Q)", currentAlphabet), "E");
    static MovingRotor movingRotorIII = new MovingRotor("III",
            new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)",
                    currentAlphabet), "V");
    static MovingRotor movingRotorIV = new MovingRotor("IV",
            new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)",
                    currentAlphabet), "J");
    static MovingRotor movingRotorV = new MovingRotor("V",
            new Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)",
                    currentAlphabet), "Z");
    static MovingRotor movingRotorVI = new MovingRotor("VI",
            new Permutation("(AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK)",
                    currentAlphabet), "ZM");
    static MovingRotor movingRotorVII = new MovingRotor("VII",
            new Permutation("(ANOUPFRIMBZTLWKSVEGCJYDHXQ)",
                    currentAlphabet), "ZM");
    static MovingRotor movingRotorVIII = new MovingRotor("VIII",
            new Permutation("(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)",
                    currentAlphabet), "ZM");
    static FixedRotor fixedRotorBeta = new FixedRotor("Beta",
            new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)",
                    currentAlphabet));
    static FixedRotor fixedRotorGamma = new FixedRotor("Gamma",
            new Permutation("(AFNIRLBSQWVXGUZDKMTPCOYJHE)", currentAlphabet));
    static Reflector reflectorB = new Reflector("B",
            new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) "
                    + "(HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", currentAlphabet));
    static Reflector reflectorC = new Reflector("C",
            new Permutation("(AR) (BD) (CO) (EJ) (FN) (GT) "
                    + "(HK) (IV) (LM) (PW) (QZ) (SX) (UY)", currentAlphabet));

    @Test
    public void checkFixedRotor() {
        Alphabet newAlphabet = new Alphabet();

        Permutation newPermutation = new Permutation("(AELTPHQXRU) (BKNW) "
                + "(CMOY) (DFG) (IV) (JZ) (S)", newAlphabet);
        FixedRotor myRotor = new FixedRotor("I",
                new Permutation("(AELTPHQXRU) (BKNW) "
                        + "(CMOY) (DFG) (IV) (JZ) (S)", newAlphabet));

        assertEquals("Error: A",
                4, myRotor.convertForward(0));
        assertEquals("Mistakes were made: A",
                20, myRotor.convertBackward(0));
        assertEquals("The S thing was wrong: forwards",
                18, myRotor.convertForward(18));
        assertEquals("The S thing was wrong: backwards",
                18, myRotor.convertBackward(18));
        myRotor.advance();
        assertEquals("Error A going Forwards",
                4, myRotor.convertForward(0));
        assertEquals("Error A going Backwards",
                20, myRotor.convertBackward(0));
        assertEquals("Error W permuting forward",
                1, myRotor.convertForward(22));
        assertEquals("Error W permuting backwards",
                13, myRotor.convertBackward(22));
    }
}
