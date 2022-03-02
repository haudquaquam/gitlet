package enigma;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
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
        if (allRotors.size() != numRotors) {
            throw new EnigmaException("Error in size of allRotors!");
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
        if (k >= numRotors()) {
            throw new EnigmaException("Index out of bounds, cannot get rotor!");
        }
        return _rotorsInSlots.get(k); // this one or _rotors array?
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            int k = 0;
            while (k < _allRotors.size()) {
                if (_allRotors.get(k).name() == rotors[i] && !(_rotorsInSlots.contains(rotors[i]))) {
                    _rotorsInSlots.add(i, _allRotors.get(k));
                    break;
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
        if (!(_rotorsInSlots.get(_rotorsInSlots.size() - 1) instanceof MovingRotor)) {
            throw error("Rightmost rotor is not a MovingRotor!");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (char character : setting.toCharArray()) {
            if (!(alphabet().contains(character))) {
                throw error("Setting string contains characters not in alphabet!");
            }
        }
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("Incorrect length of setting string!");
        }
        for (int i = 0; i < _rotorsInSlots.size() - 1; i++) {
            _rotorsInSlots.get(i + 1).set(setting.charAt(i));
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
        int firstMovingRotorIndex = 0;
        while (index < _rotorsInSlots.size()) {
            if (_rotorsInSlots.get(index) instanceof MovingRotor) {
                firstMovingRotorIndex = index;
                break;
            }
            index++;
        }

        int rotorIndex = firstMovingRotorIndex;
        int rightmostIndex = _rotorsInSlots.size() - 1;
        while (rotorIndex < rightmostIndex) {
            Rotor currentRotor = _rotorsInSlots.get(rotorIndex);
            Rotor nextRotor = _rotorsInSlots.get(rotorIndex + 1);
            if (nextRotor.atNotch()) {
                currentRotor.advance();
                if (rotorIndex != rightmostIndex) {
                    nextRotor.advance();
                }
                currentRotor.advance();
                rotorIndex += 2;
            } else {
                rotorIndex++;
            }
        }
        _rotorsInSlots.get(rightmostIndex).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        return c; // FIXME
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        return ""; // FIXME
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    private int _numRotors;
    private int _pawls;
    private ArrayList<Rotor> _allRotors;
//  private ArrayList<Rotor> _allRotorsArray = new ArrayList<Rotor>(_allRotors);
    private ArrayList<Rotor> _rotorsInSlots = new ArrayList<Rotor>();
    private Permutation _plugboard;

}
