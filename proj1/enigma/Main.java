package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Eduardo Huerta-Mercado
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        // FIXME
        _machine = readConfig();
        if (!_input.hasNextLine()) {
            throw new EnigmaException("Invalid Input.");
        }
        String line = _input.nextLine();
        if (!line.contains("*")) {
            throw new EnigmaException("Invalid Configuration.");
        }
        while (_input.hasNextLine()) {
            String setting = line;
            String rtn;
            setUp(_machine, setting);
            if (!_input.hasNextLine()) {
                throw new EnigmaException("Invalid Input.");
            }
            line = _input.nextLine().toUpperCase();
            while (!line.contains("*")) {
                String lineConv = line.replaceAll(" ", "");
                if (line.equals("")) {
                    _output.println();
                } else {
                    rtn = _machine.convert(lineConv);
                    printMessageLine(rtn);
                }
                if (!_input.hasNextLine()) {
                    line = "*";
                } else {
                    line = (_input.nextLine()).toUpperCase();
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        //* try {
            // FIXME
         //   _alphabet = new Alphabet();
        //    return new Machine(_alphabet, 2, 1, null);
        //} catch (NoSuchElementException excp) {
        //    throw error("configuration file truncated");
        // }
        try {
            String alphabet = _config.next();
            if (alphabet.contains("(") || alphabet.contains(")") || alphabet.contains("*")) {
                throw new EnigmaException("Wrong config format");
            }
            _alphabet = new Alphabet(alphabet);

            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong config format");
            }
            int numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Wrong config format");
            }
            int pawls = _config.nextInt();
            temp = (_config.next()).toUpperCase();
            while (_config.hasNext()) {
                name = temp;
                notches = (_config.next()).toUpperCase();
                _allTheRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allTheRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }

    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        //try {
         //   return null; // FIXME
        //} catch (NoSuchElementException excp) {
        //    throw error("bad rotor description");
        //}
        try {
            String cycles = "";
            _tempName = (_config.next()).toUpperCase();
            while (_tempName.contains("(") && _config.hasNext()) {
                if (!_tempName.contains(")")) {
                    throw new EnigmaException("Bad config.");
                }
                cycles = cycles.concat(_tempName + " ");
                _tempName = (_config.next()).toUpperCase();
            }
            if (!_config.hasNext()) {
                cycles = cycles.concat(_tempName + " ");
            }
            Permutation perm = new Permutation(cycles, _alphabet);
            char rotorType = _rotorTypeNotch.charAt(0);
            String notches;
            if (rotorType == 'M') {
                notches = _rotorTypeNotch.substring(1);
                if (notches == null) {
                    throw new EnigmaException("Invalid Notches for Moving");
                } else {
                    return new MovingRotor(_rotorName, perm, notches);
                }
            } else if (rotorType == 'R') {
                return new Reflector(_rotorName, perm);
            } else if (rotorType == 'N') {
                return new FixedRotor(_rotorName, perm);
            } else {
                throw new EnigmaException("Invalid Rotor Type");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        // FIXME
        String[] settingSplit = settings.split(" ");
        if (settingSplit.length - 2 < _numRotors) {
            throw new EnigmaException("Invalid rotor numbers.");
        }
        String[] rotors = new String[_numRotors];
        System.arraycopy(settingSplit, 1, rotors, 0, _numRotors);

        for (int i = 0; i < rotors.length - 2; i += 1) {
            for (int j = i + 1; j < rotors.length - 1; j += 1) {
                if (rotors[i].equals(rotors[j])) {
                    throw new EnigmaException("Invalid Rotors: Repeated");
                }
            }
        }
        M.insertRotors(rotors);
        Rotor[] rotRtn = M.rotors();
        if (!rotRtn[0].reflecting()) {
            throw new EnigmaException("Invalid reflector.");
        }
        int d = _numRotors - _numPawls;
        for (int t = 1; t < d; t += 1) {
            if (rotRtn[t].rotates()) {
                throw new EnigmaException("Moving can on the right of fixed");
            }
        }
        for (int m = d; m < _numRotors; m += 1) {
            if (!rotRtn[m].rotates()) {
                throw new EnigmaException("Invalid fixed rotors");
            }
        }

        String ogSetting = settingSplit[_numRotors + 1];
        if (ogSetting.length() < _numRotors - 1) {
            throw new EnigmaException("Invalid Settings");
        }
        M.setRotors(ogSetting);
        String plugboardCycle = "";
        Permutation plugboardPerm;
        if (settingSplit.length > _numRotors + 2) {
            for (int i = _numRotors + 2; i < settingSplit.length; i += 1) {
                plugboardCycle = plugboardCycle + settingSplit[i];
            }
            plugboardPerm = new Permutation(plugboardCycle, _alphabet);
            M.setPlugboard(plugboardPerm);
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        // FIXME
        for (int i = 0; i < msg.length(); i += 1) {
            if (i % 6 == 0) {
                msg = msg.substring(0, i) + " " + msg.substring(i);
            }
        }
        _output.println(msg.trim());
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** The Machine we are working on (additional field). */
    private Machine _machine;

    /** Number of Rotors (additional field). */
    private int _numRotors;

    /** Number of Pawls (additional field). */
    private int _numPawls;

    /** Collections of all reflectors. */
    private ArrayList<String> _reflectors = new ArrayList<String>();

    /** Collection of all moving rotors. */
    private ArrayList<String> _moving = new ArrayList<String>();

    /** Collection of all fixed rotors. */
    private ArrayList<String> _fixed = new ArrayList<String>();

    /** Length of configuration file. */
    private int _lenConfig;

    /** Temporary name. */
    private String _tempName;

    /** _rotorName. */
    private String _rotorName;

    /** rotorType. */
    private String _rotorTypeNotch;
    private String perm;

    /** Name of current rotor. */
    private String name;

    /** Temporary string that is set to NEXT token of _config. */
    private String temp;

    /** Type and notches of current rotor. */
    private String notches;
    private ArrayList<Rotor> _allTheRotors = new ArrayList<>();
}
