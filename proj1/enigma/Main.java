package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.LinkedList;
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
        Machine temp = readConfig();
        String charTemp = _input.nextLine();
        //String compressedMessage = "";

        while (_input.hasNext()) {
            String settingLine = charTemp;
            //if (settingLine.charAt(0) != '*'){
            //    System.out.println("incorrect");
            //}
            setUp(temp, settingLine);
            charTemp = _input.nextLine();

            while (charTemp.charAt(0) != '*'){
                String charTemp2 = "";
                Scanner charTempScanner = new Scanner(charTemp);
                while ((charTempScanner.hasNext())){
                    charTemp2 = charTemp2 + charTempScanner.next();
                }
                String report = temp.convert(charTemp2);
                for (int i = 1; i <= report.length(); i++){
                    _output.append(report.charAt(i - 1));
                    if ( i % 5 == 0){
                        _output.append(' ');
                    }
                }
                _output.append("\r\n");
                if (_input.hasNext()){
                    charTemp = _input.nextLine();
                } else{
                    break;
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
            // FIXME

        try {
            _alphabet = new Alphabet(_config.nextLine());
            _numRotors = _config.nextInt();
            _numPawls  = _config.nextInt();

            LinkedList<Rotor> NewallRotors = new LinkedList<>();
            charTemp = _config.next();
            while (_config.hasNext()){
                NewallRotors.add(readRotor());
            }
            return new Machine(_alphabet, _numRotors, _numPawls, NewallRotors);

        } catch (NoSuchElementException exception) {
            throw error("wrong");
        }

    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        // FIXME
        String tempPermutations = "";
        String rotorName = "";
        String rotorNotches = "";

        try {
            rotorName = charTemp;
            charTemp = _config.next();
            rotorNotches = charTemp;
            charTemp = _config.next();

            while (charTemp.charAt(0) == '('){
                tempPermutations = tempPermutations + charTemp;
                if (!_config.hasNext()){
                    break;
                }
                charTemp = _config.next();
            }
            if (rotorNotches.charAt(0) == 'M'){
                return new MovingRotor(rotorName, new Permutation(tempPermutations, _alphabet), rotorNotches.substring(1));
            } else if (rotorNotches.charAt(0) == 'N'){
                return new FixedRotor(rotorName, new Permutation(tempPermutations, _alphabet));
            } else if (rotorNotches.charAt(0) == 'R'){
                return new Reflector(rotorName, new Permutation(tempPermutations, _alphabet));
            }else{
                System.out.println("incorrect");
            }
            return null;


        } catch (NoSuchElementException excp) {
            throw error("incorrect rotor");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        // FIXME
        String[] insertedRotos = new String[_numRotors];
        Scanner settingLineScanner = new Scanner(settings);
        String plugboardString;

        settingLineScanner.next();

        for (int i = 0; i < _numRotors; i++){
            String tempString = settingLineScanner.next();
            insertedRotos[i] = "Rotor " + tempString;
        }
        String rotorSettings = settingLineScanner.next();
        plugboardString = settingLineScanner.nextLine();

        M.insertRotors(insertedRotos);
        M.setPlugboard(new Permutation(plugboardString, _alphabet));
        M.setRotors(rotorSettings);

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        // FIXME
        for (int i = 1; i < msg.length(); i++) {
            //System.out.println(msg.charAt(i - 1));
            if (i % 5 == 0) {
                System.out.println(" ");
            }
        }
        System.out.println("");;
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
    /** Number of Rotors (additional field). */
    private int _numRotors;

    /** Number of Pawls (additional field). */
    private int _numPawls;

    /** current string */
    String charTemp;
}
