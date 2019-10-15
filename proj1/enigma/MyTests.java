package enigma;
import net.sf.saxon.style.XSLOutput;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
        testMachine();
    }

    @Test
    public void testPermutations() {
        Alphabet permutationAlphabet = new Alphabet("JOHNCEADISMWZ");
        Permutation myPermutation =
                new Permutation("(ANDHISMEW) (JO) (C)", permutationAlphabet);
        assertEquals("the size is wrong", 13, myPermutation.size());
        System.out.println("JOHNCEADISMWZ".indexOf('Z'));
        assertEquals("Permute A", 3, myPermutation.permute(6));
        assertEquals("Permute A (char)", 'N', myPermutation.permute('A'));

        assertEquals("Permute O", 0, myPermutation.permute(1));
        assertEquals("Permute O (char)", 'J', myPermutation.permute('O'));

        assertEquals("Permute C", 3, myPermutation.permute(6));
        assertEquals("Permute C (char)", 'N', myPermutation.permute('A'));

        assertEquals("Permute Z", 12, myPermutation.permute(12));
        assertEquals("Permute Z (char)", 'Z', myPermutation.permute('Z'));
    }

    @Test
    public void testInverse() {
        Alphabet inverseAlphabet = new Alphabet("JOHNCEADISMWZ");
        Permutation myPermutation =
                new Permutation("(ANDHISMEW) (JO) (C)", inverseAlphabet);
        assertEquals("the size is wrong", 13, myPermutation.size());
        System.out.println("JOHNCEADISMWZ".indexOf('Z'));
        assertEquals("Invert A", 11, myPermutation.invert(6));
        assertEquals("Invert A (char)", 'W', myPermutation.invert('A'));

        assertEquals("Invert O", 0, myPermutation.invert(1));
        assertEquals("Invert O (char)", 'J', myPermutation.invert('O'));

        assertEquals("Invert C", 4, myPermutation.invert(4));
        assertEquals("Invert C (char)", 'C', myPermutation.invert('C'));

        assertEquals("Invert Z", 12, myPermutation.invert(12));
        assertEquals("Invert Z (char)", 'Z', myPermutation.invert('Z'));
    }

    @Test
    public void checkFixedRotor() {
        Alphabet newAlphabet = new Alphabet();

        Permutation newPermutation = new Permutation("(AELTPHQXRU) (BKNW) "
                + "(CMOY) (DFG) (IV) (JZ) (S)", newAlphabet);
        FixedRotor myRotor = new FixedRotor("I",
                new Permutation("(AELTPHQXRU) (BKNW) "
                        + "(CMOY) (DFG) (IV) (JZ) (S)", newAlphabet));

        assertEquals("Mistakes were made: A",
                4, myRotor.convertForward(0));
        assertEquals("Mistakes were made: A",
                20, myRotor.convertBackward(0));
        assertEquals("The S thing was wrong: fornwards",
                18, myRotor.convertForward(18));
        assertEquals("The S thing was wrong: backwards",
                18, myRotor.convertBackward(18));
        myRotor.advance();
        assertEquals("Mistakes were made A forwards",
                4, myRotor.convertForward(0));
        assertEquals("Mistakes were made A backwards",
                20, myRotor.convertBackward(0));
        assertEquals("Mistakes were made W forwards",
                1, myRotor.convertForward(22));
        assertEquals("Mistakes were made W backwards",
                13, myRotor.convertBackward(22));
    }

    @Test
    public static void testMachine() {
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
        String[] insertedRotors = new String[] {reflectorB.toString(), fixedRotorBeta.toString(),
                movingRotorIII.toString(), movingRotorIV.toString(), movingRotorI.toString()};
        myMachine.insertRotors(insertedRotors);
//        System.out.print("inserted Rotors: ");
//        for (int i = 0; i < insertedRotors.length; i += 1) {
//            System.out.print(insertedRotors[i]);
//        }
//        System.out.println("");
        myMachine.setRotors("AXLE");
        myMachine.setPlugboard(new Permutation("(YF) (ZH)", currentAlphabet));

        //System.out.println(currentAlphabet.toChar(myMachine.convert(0)));
//        System.out.println(currentAlphabet.toChar(myMachine.convert(2)));
//        System.out.println(currentAlphabet.toChar(myMachine.convert(0)));
//        System.out.println(currentAlphabet.toChar(myMachine.convert(2)));
//        System.out.println(currentAlphabet.toChar(myMachine.convert(12)));
//        System.out.println(currentAlphabet.toChar(myMachine.convert(0)));

        /** the following couple of lines of code are according to the example provided by the spec. */
        String recordedString = "";
        String tempChar = myMachine.convert("Y");
        System.out.println(tempChar);
        recordedString += tempChar;

        for (int i = 0; i < 11; i += 1) {
            myMachine.convert("A");
        }
        System.out.println(myMachine.settings());

        myMachine.convert("A");
        System.out.println(myMachine.settings());

        for (int i= 0; i < 597; i += 1){myMachine.convert("A");}
        System.out.println(myMachine.settings());

        myMachine.convert("A");
        System.out.println(myMachine.settings());

        myMachine.convert("A");
        System.out.println(myMachine.settings());
    }


    /** These are the rotors :D */
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
}
