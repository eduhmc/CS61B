package enigma;

import java.util.HashMap;
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
        // FIXME
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;

        //return 0; // FIXME
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;

        //return 0; // FIXME
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        //Object[] tempAry = _allRotors.toArray();
        //if (_rotors.length != numRotors()) {
        //    throw new EnigmaException("Invalid ROTORS");
        //}
        //for (int i = 0; i < rotors.length; i += 1) {
        //    for (int j = 0; j < tempAry.length; j += 1) {
        //        if ((rotors[i]).equals(((Rotor) tempAry[j]).name())) {
        //            _rotors[i] = (Rotor) tempAry[j];
        //        }
        //    }
        //}
        // FIXME
        for (int i = 0; i < rotors.length; i++){
            for(Rotor r: _allRotors){
                if (("Rotor " + r.name()).equals(rotors[i])){
                    _rotors[i] = r;
                    _rotors[i].set(0);


                }

            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        //if (setting.length() != numRotors() - 1) {
        //    throw new EnigmaException("Invalid Setting");
        //}
        //for (int i = 1; i < numRotors(); i += 1) {
        //   if (!_alphabet.contains(setting.charAt(i - 1))) {
        //        //System.out.println(setting);
        //        //System.out.println(setting.charAt(i-1));
        //        throw new EnigmaException("Initial positions not specified");
        //    }
        //    _rotors[i].set(setting.charAt(i - 1));
        //}
        int myRotorIndex = _rotors.length -1;

        for (Rotor i : _rotors) {
            System.out.println(i);
        }
        for (int i = setting.length() -1; i >= setting.length() - numPawls(); i -= 1 ){
            _rotors[myRotorIndex].set(setting.charAt(i));
            myRotorIndex -=1;
        }

        // FIXME
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
        // FIXME
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {

        //return 0; // FIXME
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        HashSet<Integer> check = new HashSet<>();
        for (int i = _rotors.length - 1; i > 0; i -= 1) {
            if (_rotors[i - 1].rotates() && (_rotors[i].atNotch())) {
                check.add(i);
                check.add(i - 1);
            }
        }
        check.add(_rotors.length - 1);
        for (int r : check) {
            _rotors[r].advance();
        }
        for (int x = _rotors.length - 1; x >= 0; x -= 1) {
            c = _rotors[x].convertForward(c);
        }
        for (int k = 1; k < _numRotors; k += 1) {
            c = _rotors[k].convertBackward(c);
        }
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        return c;

    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            int temp = _alphabet.toInt(msg.charAt(i));
            char converted = _alphabet.toChar(convert(temp));
            result += converted;
        }
        return result;
        //return ""; // FIXME
    }

    /** Return the _rotors of this class. */
    Rotor[] rotors() {
        return _rotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    // FIXME: ADDITIONAL FIELDS HERE, IF NEEDED.
    /** Number of ROTORS. */
    private int _numRotors;
    /** Number of PAWLS. */
    private int _pawls;
    /** A Collection of all ROTORS. */
    private Collection<Rotor> _allRotors;
    /** An array of ROTORS. */
    private Rotor[] _rotors;
    /** PLUGBOARD of the class. */
    private Permutation _plugboard;

    String settings() {
        String settingString = "";
        for (Rotor r: _rotors) {
//            System.out.println("Rotor name: " + r + " | " + "r.setting: " + r.setting());
            settingString = settingString + _alphabet.toChar(r.setting());
        }
        return settingString;
    }


}

