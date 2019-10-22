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
        Machine ahora = readConfig(); temporal = _input.nextLine();
        if (temporal.charAt(0) != '*') {
            throw new EnigmaException("First line not setup.");
        }

        while (_input.hasNext()) {
            String creating = temporal;
            if (creating.charAt(0) != '*') {
                throw new EnigmaException("Incorrect");
            }
            setUp(ahora, creating);
            if (newplug.isEmpty()) {
                _output.append("\r\n");
            } else {
                if (newplug.charAt(0) == '(') {
                    temporal = _input.nextLine();
                } else {
                    temporal = newplug;
                }
            }
            while (temporal.charAt(0) != '*') {
                String justForNow = "";
                Scanner scan = new Scanner(temporal);
                while (scan.hasNext()) {
                    justForNow = justForNow
                            + scan.next();
                }
                String report = ahora.convert(justForNow);
                for (int i = 1; i <= report.length(); i += 1) {
                    _output.append(report.charAt(i - 1));
                    if ((i % 5 == 0) && !(i == report.length())){
                        _output.append(' ');
                    }
                }
                _output.append("\r\n");
                if (_input.hasNext()) {
                    temporal = _input.nextLine();
                    while (temporal.isEmpty()) {
                        _output.append("\r\n");
                        temporal = _input.nextLine();
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
            rotorsNumber = _config.nextInt(); pawlsnumber = _config.nextInt();

            LinkedList<Rotor> lknlist = new LinkedList<>();
            temporal = _config.next();

            while (_config.hasNext()) {
                lknlist.add(readRotor());
            }
            return new Machine(_alphabet, rotorsNumber, pawlsnumber, lknlist);
        } catch (NoSuchElementException excp) {
            throw error("conf file wrong");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        String permutacionActual = ""; String nombre = ""; String newNotch = "";
        try {
            nombre = temporal; temporal = _config.next();
            newNotch = temporal; temporal = _config.next();

            while (temporal.charAt(0) == '(') {
                permutacionActual = permutacionActual + temporal;
                if (!_config.hasNext()) {
                    break;
                }
                temporal = _config.next();
            }

            if (newNotch.charAt(0) == 'M') {
                return new MovingRotor(nombre,
                        new Permutation(permutacionActual, _alphabet),
                        newNotch.substring(1));
            } else if (newNotch.charAt(0) == 'N') {
                return new FixedRotor(nombre,
                        new Permutation(permutacionActual, _alphabet));
            } else if (newNotch.charAt(0) == 'R') {
                return new Reflector(nombre,
                        new Permutation(permutacionActual, _alphabet));
            } else {
                throw new EnigmaException("Incorrect");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description" + temporal);
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] insertedRotors = new String[rotorsNumber];
        Scanner creatingScanner = new Scanner(settings);
        String reportstr; creatingScanner.next();

        for (int i = 0; i < rotorsNumber; i += 1) {
            String tempString = creatingScanner.next();
            insertedRotors[i] = "Rotor " + tempString;
        }
        String rotorSettings = creatingScanner.next();
        newplug = _input.nextLine();
        if (creatingScanner.hasNext()) {
            reportstr = creatingScanner.nextLine();
            if (!reportstr.equals("")
                && reportstr.charAt(1) != '(') {
                throw new EnigmaException("Wrong settings");
            }
        } else {
            reportstr = "";
        }
        M.insertRotors(insertedRotors);
        M.setPlugboard(new Permutation(reportstr, _alphabet));
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

    /** Alphabet. */
    private Alphabet _alphabet;

    /** Number of rotors.*/
    private int rotorsNumber;

    /** Number of pawls.*/
    private int pawlsnumber;

    /** This are the input messages. */
    private Scanner _input;

    /** This is the machine configuration. */
    private Scanner _config;

    /** Gives the output of the encode and decode messages. */
    private PrintStream _output;

    /** Temporary String.*/
    private String temporal;

    /** New Line of plugboard.*/
    private String newplug;
}
