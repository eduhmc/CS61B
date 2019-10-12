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
        try {
            // FIXME
            _alphabet = new Alphabet();
            return new Machine(_alphabet, 2, 1, null);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            return null; // FIXME
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        // FIXME
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        // FIXME
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
}
