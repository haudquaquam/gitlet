package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Rae Xin
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        if (numRotors <= 1) {
            throw new EnigmaException("Not enough rotors!");
        }
        if (!((pawls >= 0) && (pawls < numRotors))) {
            throw new EnigmaException("Bad number of pawls!");
        }
        _allRotors = new ArrayList<Rotor>(allRotors);
        _pawls = pawls;
        _numRotors = numRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        if (k >= numRotors() || k < 0) {
            throw new EnigmaException("Index out of bounds, cannot get rotor!");
        }
        return _rotorsInSlots.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotorsInSlots = new ArrayList<>(0);
        for (int i = 0; i < rotors.length; i++) {
            int k = 0;
            while (k < _allRotors.size()) {
                Rotor currentAllRotor = _allRotors.get(k);
                if (currentAllRotor.name().equals(rotors[i])) {
                    if (!(_rotorsInSlots.contains(rotors[i]))) {
                        _rotorsInSlots.add(currentAllRotor);
                        break;
                    }
                }
                k++;
            }
        }
        if (!(_rotorsInSlots.get(0) instanceof Reflector)) {
            throw new EnigmaException("First rotor is not a reflector!");
        }
        if (_rotorsInSlots.size() != rotors.length) {
            throw error("Attempted to insert invalid rotors!");
        }
        Rotor rightmostRotor = _rotorsInSlots.get(_rotorsInSlots.size() - 1);
        if (!(rightmostRotor instanceof MovingRotor)) {
            throw error("Rightmost rotor is not a MovingRotor!");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (char character : setting.toCharArray()) {
            if (!(alphabet().contains(character))) {
                throw error("Invalid setting!");
            }
        }
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Incorrect length of setting string!");
        }
        for (int i = 0; i < _rotorsInSlots.size() - 1; i++) {
            getRotor(i + 1).set(setting.charAt(i));
        }
        int difference = _numRotors - numPawls();
        for (int i = 0; i < numRotors(); i++) {
            if (i < difference) {
                if (!(_rotorsInSlots.get(i) instanceof FixedRotor)) {
                    throw error("Rotor should be fixed rotor!");
                }
            } else {
                if (!(_rotorsInSlots.get(i) instanceof MovingRotor)) {
                    throw error("Rotor should be moving rotor!");
                }
            }
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        ArrayList<String> plugboardCycleList = plugboard.permutationArrayList();
        for (int i = 0; i < plugboardCycleList.size(); i++) {
            if (plugboardCycleList.get(i).length() != 2) {
                throw new EnigmaException("Plugboard contains invalid cycles!");
            }
        }
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        int index = 0;
        int rightmostIndex = _rotorsInSlots.size() - 1;
        while (index < rightmostIndex) {
            Rotor currentRotor = getRotor(index);
            Rotor nextRotor = getRotor(index + 1);
            if (currentRotor.rotates() && nextRotor.atNotch()) {
                currentRotor.advance();
                if (index != rightmostIndex - 1) {
                    nextRotor.advance();
                }
                index += 2;
            } else {
                index++;
            }
        }
        getRotor(rightmostIndex).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {

        int i = _rotorsInSlots.size() - 1;
        while (i >= 0) {
            c = getRotor(i).convertForward(c);
            i--;
        }
        i = 1;
        while (i < _rotorsInSlots.size()) {
            c = getRotor(i).convertBackward(c);
            i++;
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char currentChar = msg.charAt(i);
            int alphabetIndexOfCurrentChar = _alphabet.toInt(currentChar);
            result += _alphabet.toChar(convert(alphabetIndexOfCurrentChar));
        }
        return result;
    }

    ArrayList<String> allRotorNames() {
        ArrayList<String> allRotorNames = new ArrayList<>();
        for (Rotor rotor : _allRotors) {
            allRotorNames.add(rotor.name());
        }
        return allRotorNames;
    }

    ArrayList<Rotor> getRotorsInSlots() {
        return _rotorsInSlots;
    }



    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of my rotors. */
    private int _numRotors;

    /** Number of my pawls. */
    private int _pawls;

    /** List of all rotors available. */
    private ArrayList<Rotor> _allRotors;

    /** List of active rotors that have been inserted. */
    private ArrayList<Rotor> _rotorsInSlots;

    /** My plugboard. */
    private Permutation _plugboard;

}
