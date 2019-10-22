package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Eduardo Huerta Mercado
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
        Machine currentMachine = readConfig();
        tempFirstString = _input.nextLine();
        if (tempFirstString.charAt(0) != '*') {
            throw new EnigmaException("First line not setup.");
        }
        String compressedMessage = "";

        while (_input.hasNext()) {
            String settingLine = tempFirstString;
            if (settingLine.charAt(0) != '*') {
                System.out.println("Incorrect");
            }

            setUp(currentMachine, settingLine);

            if (plugboardOrLine.charAt(0) == '(') {
                tempFirstString = _input.nextLine();
            } else {
                tempFirstString = plugboardOrLine;
            }
            while (tempFirstString.charAt(0) != '*') {

                String trTempFirstString = "";
                Scanner tempFirstStringScanner = new Scanner(tempFirstString);

                while (tempFirstStringScanner.hasNext()) {
                    trTempFirstString = trTempFirstString
                            + tempFirstStringScanner.next();
                }
                String returnString = currentMachine.convert(trTempFirstString);
                for (int i = 1; i <= returnString.length(); i += 1) {
                    _output.append(returnString.charAt(i - 1));
                    if (i % 5 == 0) {
                        _output.append(' ');
                    }
                }
                _output.append("\r\n");
                if (_input.hasNext()) {
                    tempFirstString = _input.nextLine();
                    while (tempFirstString.isEmpty()) {
                        _output.append("\r\n");
                        tempFirstString = _input.nextLine();
                    }
                } else {
                    break;
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine());
            numRotors = _config.nextInt();
            numPawls = _config.nextInt();

            LinkedList<Rotor> allRotors = new LinkedList<>();
            tempFirstString = _config.next();

            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("conf file wrong");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        String tempPermutations = "";
        String rotorName = "";
        String rotorNotches = "";
        try {
            rotorName = tempFirstString;
            tempFirstString = _config.next();
            rotorNotches = tempFirstString;
            tempFirstString = _config.next();

            while (tempFirstString.charAt(0) == '(') {
                tempPermutations = tempPermutations + tempFirstString;
                if (!_config.hasNext()) {
                    break;
                }
                tempFirstString = _config.next();
            }

            if (rotorNotches.charAt(0) == 'M') {
                return new MovingRotor(rotorName,
                        new Permutation(tempPermutations, _alphabet),
                        rotorNotches.substring(1));
            } else if (rotorNotches.charAt(0) == 'N') {
                return new FixedRotor(rotorName,
                        new Permutation(tempPermutations, _alphabet));
            } else if (rotorNotches.charAt(0) == 'R') {
                return new Reflector(rotorName,
                        new Permutation(tempPermutations, _alphabet));
            } else {
                System.out.println("HURR BERR DURR MAH DUUURRRDD");
            }
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description" + tempFirstString);
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] insertedRotors = new String[numRotors];
        Scanner settingLineScanner = new Scanner(settings);
        String plugboardString;
        settingLineScanner.next();

        for (int i = 0; i < numRotors; i += 1) {
            String tempString = settingLineScanner.next();
            insertedRotors[i] = "Rotor " + tempString;
        }

        String rotorSettings = settingLineScanner.next();


        plugboardOrLine = _input.nextLine();
        if (settingLineScanner.hasNext()) {

            plugboardString = settingLineScanner.nextLine();
        } else {

            plugboardString = "";
        }


        M.insertRotors(insertedRotors);
        M.setPlugboard(new Permutation(plugboardString, _alphabet));
        M.setRotors(rotorSettings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 1; i <= msg.length(); i += 1) {
            System.out.print(msg.charAt(i - 1));
            if (i % 5 == 0) {
                System.out.print(" ");
            }
        }
        System.out.println("");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** My new Enigma machine. */
    private Machine _myMachine;

    /** Number of rotors.*/
    private int numRotors;

    /** Number of pawls.*/
    private int numPawls;

    /** The current string of _config.next().*/
    private String tempFirstString;

    /** The next line of the code, different depending on whether there
     * is a plugboard after the settings.*/
    private String plugboardOrLine;
}
