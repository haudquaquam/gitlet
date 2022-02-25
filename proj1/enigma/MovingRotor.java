package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Rae Xin
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        set(0);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        if (atNotch()) {
            set(_settingPositionInt + 1);
        }
    }

    @Override
    String notches() {
        return _notches;
    }

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED

}
