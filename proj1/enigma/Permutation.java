package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Eduardo Huerta-Mercado
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _ciclos = StringToArray(cycles);

        // FIXME
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        boolean x = true;

        // FIXME
        }


    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
        //return 0; // FIXME
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int preal = wrap(p); char pstring = _alphabet.toChar(preal);
        String str = ""; int index = 0;
        for (int i = 0; i < _ciclos.size(); i++){
            if (_ciclos.get(i).indexOf(pstring) != -1){
                index = _ciclos.get(i).indexOf(pstring);
                str = _ciclos.get(i);
                return _alphabet.toInt(str.charAt((index + 1) % str.length()));
            }
        }
        return _alphabet.toInt(pstring);


        //return 0;  // FIXME
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int creal = wrap(c); char cstring = _alphabet.toChar(creal);
        String str2 = ""; int index2 = 0;
        for (int i = 0; i < _ciclos.size(); i++){
            if (_ciclos.get(i).indexOf(cstring) != -1){
                index2 = _ciclos.get(i).indexOf(cstring);
                str2 = _ciclos.get(i);
                return _alphabet.toInt(str2.charAt((index2 - 1) % str2.length()));
            }
        }
        return _alphabet.toInt(cstring);

        //return 0;  // FIXME
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int inchar = _alphabet.toInt(p);
        int sol = permute(inchar);
        return _alphabet.toChar(sol);

        // return 0;  // FIXME
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        int inchar2 = _alphabet.toInt(c);
        int sol2 = invert(inchar2);
        return _alphabet.toChar(sol2);

        //return 0;  // FIXME
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {

        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int contador = 0;
        for (int i = 0; i < _ciclos.size(); i += 1) {
            contador = contador + _ciclos.get(i).length();
        }
        return contador == _alphabet.size();
        //return true;  // FIXME
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    private ArrayList<String> _ciclos;

    static ArrayList<String> StringToArray(String cycles){
        ArrayList<String> final_array = new ArrayList<>();
        int start_location = 0;
        for (int i = 0; i < cycles.length(); i++){
            char open_paren = '(';
            char close_paren = ')';
            if (cycles.charAt(i) == open_paren){
                start_location = i + 1;
            }
            if (cycles.charAt(i) == close_paren){
                final_array.add(cycles.substring(start_location, i));
            }
        }
        return final_array;
    }


    public static void main(String[] args) {
        ArrayList<String> a = StringToArray("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)");
        System.out.println();
    }


}
