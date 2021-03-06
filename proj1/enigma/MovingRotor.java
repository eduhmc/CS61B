package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Eduardo Huerta Mercado
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        _permutation = perm;
    }
    @Override
    boolean rotates() {

        return true;
    }


    @Override
    void advance() {
        super.set(_permutation.wrap(super.setting() + 1));
    }
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            if (this.setting() == alphabet().toInt(_notches.charAt(i))) {
                return true;
            }
        }
        return false;
    }
    /** Permutation. */
    private Permutation _permutation;
    /** Notches. */
    private String _notches;
}
