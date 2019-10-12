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
        _perm = perm;
        // FIXME
    }
    @Override
    boolean rotates() {

        return true;
    }

    // FIXME?

    @Override
    void advance() {
        super.set(_perm.wrap(super.setting() + 1));
        // FIXME
    }
    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i += 1) {
            if (this.setting() == alphabet().toInt(_notches.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    /** Notches. */
    private String _notches;
    /** Permutation. */
    private Permutation _perm;

}
