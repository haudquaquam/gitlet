package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Rae Xin
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
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
        _skippedFlag = false;
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
        _mach = readConfig();
        if (_input.hasNext("\\*")) {
            processHelper();
        } else {
            throw error("Input file formatted incorrectly, lacks \\*!");
        }
    }

    /** Process settings from _input using SCANNERLINE. */
    private void processSettings(String scannerLine) {
        Scanner settingsLine = new Scanner(scannerLine);

        ArrayList<String> rotorNamesArray = new ArrayList<>();

        String currentToken = settingsLine.next();
        if (currentToken.equals("*")) {
            currentToken = settingsLine.next();
        } else if (currentToken.charAt(0) == '*') {
            currentToken = currentToken.substring(1);
        } else {
            throw error("Input file does not start with \\*!");
        }

        ArrayList<String> machAllRotorNames = _mach.allRotorNames();
        while (machAllRotorNames.contains(currentToken)) {
            if (rotorNamesArray.contains(currentToken)) {
                throw error("Repeated rotor!");
            }
            rotorNamesArray.add(currentToken);
            currentToken = settingsLine.next();
        }

        String[] rotorNamesList = rotorNamesArray.toArray(new String[0]);
        _mach.insertRotors(rotorNamesList);
        String setting = currentToken;
        if (!(setting.length() == rotorNamesArray.size() - 1)) {
            throw error("Wrong number of settings!");
        }
        setUp(_mach, setting);

        if (settingsLine.hasNext() && !(settingsLine.hasNext("\\((.*?)\\)"))) {
            String ring = settingsLine.next();
            setRingSetting(ring);
        }

        String plugboardString = "";
        while (settingsLine.hasNext("\\((.*?)\\)")) {
            currentToken = settingsLine.next();
            plugboardString += currentToken;
        }
        _mach.setPlugboard(new Permutation(plugboardString, _alphabet));
    }

    /** Implements ring setting. */
    private void setRingSetting(String ring) {
        String ringSetting = ring;
        int numReflectors = 0;
        for (int i = 0; i < _mach.getRotorsInSlots().size(); i++) {
            Rotor currentRotor = _mach.getRotor(i);
            if (!(currentRotor.reflecting())) {
                char currentRingSetting = ringSetting.charAt(i - numReflectors);
                currentRotor.setRingSetting(currentRingSetting);
            } else {
                numReflectors++;
            }
        }
    }

    /** Process lines from _input. */
    private void processHelper() {
        while (_input.hasNextLine()) {
            String currentLine = _input.nextLine();
            if (!(currentLine.equals("")) && currentLine.charAt(0) == '*') {
                processSettings(currentLine);
            } else {
                currentLine = currentLine.replaceAll("\\s+", "");
                String converted = _mach.convert(currentLine);
                printMessageLine(converted);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alpha = _config.next();
            try {
                Integer.parseInt(alpha);
                if (alpha.length() < 4) {
                    throw error("Alphabet is numeric!");
                }
            } catch (NumberFormatException nfe) {
                String hello = "there is nothing wrong";
            }
            _alphabet = new Alphabet(alpha);
            int numRotors = 0;
            int pawls = 0;
            try {
                numRotors = Integer.parseInt(_config.next());
                pawls = Integer.parseInt(_config.next());
            } catch (NumberFormatException nfe) {
                throw error("Non-int values of numRotors or pawls!");
            }
            ArrayList<Rotor> allRotors = new ArrayList<>();

            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }

            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        Permutation emptyPerm = new Permutation("", _alphabet);
        Rotor returnRotor = new Rotor("default", emptyPerm);
        try {
            String rotorName = _config.next();
            String cycles = "";
            String rotorType = _config.next();

            while (_config.hasNext("\\((.*?)\\)")) {
                String currentToken = _config.next();
                cycles += currentToken;
            }
            Permutation perm = new Permutation(cycles, _alphabet);

            char mrn = rotorType.charAt(0);
            if (mrn == 'M') {
                String notches = rotorType.substring(1);
                returnRotor = new MovingRotor(rotorName, perm, notches);
            } else if (mrn == 'R') {
                returnRotor = new Reflector(rotorName, perm);
            } else if (mrn == 'N') {
                returnRotor = new FixedRotor(rotorName, perm);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
        return returnRotor;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg != "") {
            while (msg.length() > 5) {
                _output.print(msg.substring(0, 5) + " ");
                msg = msg.substring(5);
            }
            _output.print(msg);
            _output.println();
        } else {
            _output.println();
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** Machine for this config. */
    private Machine _mach;

    /** Stupid boolean for stupid problem. */
    private boolean _skippedFlag;
}
