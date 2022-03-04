package enigma;

import static enigma.EnigmaException.error;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Rae Xin
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        set(0);
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _settingPositionInt;
    }

    void addSetting(int posn) {
        set(permutation().wrap(posn + setting()));
    }

    void addSetting(char cposn) {
        if (!(alphabet().contains(cposn))) {
            throw error("Setting %c not in alphabet!", cposn);
        }
        addSetting(alphabet().toInt(cposn));
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        if (posn >= alphabet().size()) {
            throw error("Position index of %d is out of bounds!", posn);
        }
        _settingPositionInt = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        if (!(alphabet().contains(cposn))) {
            throw error("Setting %c not in alphabet!", cposn);
        }
        set(alphabet().toInt(cposn));
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        p += setting();
        int result = permutation().permute(p);
        result = permutation().wrap(result - setting());
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        e += setting();
        int result = permutation().invert(e);
        result = permutation().wrap(result - setting());
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    /** Returns the positions of the notches, as a string giving the letters
     *  on the ring at which they occur. */
    String notches() {
        return "";
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    int getSettingInt() {
        return _settingPositionInt;
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** My notches. */
    private String _notches;

    /** My setting as an integer. */
    private int _settingPositionInt;

}
