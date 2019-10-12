package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Eduado Huerta Mercado
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
        // FIXME
    }

    /** Return my name. */
    String name() {

        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {

        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {

        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {

        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {

        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {

        return false;
    }

    /** Return my current setting. */
    int setting() {

        return _setting; // FIXME
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = mod_setting(posn, alphabet().size());
        // FIXME
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _setting = alphabet().toInt(cposn);
        // FIXME
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int conversion = _permutation.permute(p + _setting % size());
        return mod_setting(conversion - _setting, size());

        //return 0;  // FIXME
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int conversion = _permutation.invert(e + _setting % size());
        return mod_setting(conversion - _setting, size());

        //return 0;  // FIXME
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {

        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    private int _setting;
    int mod_setting(int p, int cycle_size) {
        int r = p % cycle_size;
        if (r < 0) {
            r += cycle_size;
        }
        return r;
    }

}
