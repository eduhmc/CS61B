package enigma;


import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Eduardo Huerta-Mercado
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;

    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;

    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        boolean checker;
        for (int i = 0; i < rotors.length; i++) {
            checker = false;
            for (Rotor r : _allRotors) {
                if (("Rotor " + r.name()).equals(rotors[i])) {
                    _rotors[i] = r;
                    _rotors[i].set(0);
                    checker = true;
                }
            }
            if (!checker) {
                throw new EnigmaException("Incorrect");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {

        for (int i = setting.length() - 1; i >= 0; i -= 1) {
            _rotors[i + 1].set(setting.charAt(i));
        }
        if (!(_rotors[0] instanceof Reflector)) {
            throw new EnigmaException("first rotor must be a reflector");
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the finalstring of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        HashSet<Integer> prueba = new HashSet<>();
        for (int i = _rotors.length - 1; i > 0; i--) {
            if (_rotors[i - 1].rotates() && (_rotors[i].atNotch())) {
                prueba.add(i);
                prueba.add(i - 1);
            }
        }
        prueba.add(_rotors.length - 1);
        for (int x : prueba) {
            _rotors[x].advance();
        }
        for (int j = _rotors.length - 1; j >= 0; j--) {
            c = _rotors[j].convertForward(c);
        }
        for (int y = 1; y < _numRotors; y++) {
            c = _rotors[y].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String finalstring = "";
        for (int i = 0; i < msg.length(); i += 1) {
            if (!_alphabet.contains(msg.charAt(i))) {
                throw new EnigmaException("Wrong character");
            }
            int antes = _alphabet.toInt(msg.charAt(i));
            char despues = _alphabet.toChar(convert(antes));
            finalstring += despues;
        }
        return finalstring;
    }
    /** Return rotor setting in order to get the char. */
    String rotorSettings() {
        String newSetting = "";
        for (Rotor i: _rotors) {
            newSetting = newSetting + _alphabet.toChar(i.setting());
        }
        return newSetting;
    }
    /** Return the _rotors of this class. */
    Rotor[] rotors() {
        return _rotors;
    }
    /** Alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _pawls;
    /** A Collection of all rotors. */
    private Collection<Rotor> _allRotors;
    /** An array of rotors. */
    private Rotor[] _rotors;
    /** The plugboard. */
    private Permutation _plugboard;

}

