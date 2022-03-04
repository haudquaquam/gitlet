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
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        set(permutation().wrap(getSettingInt() + 1));
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    boolean atNotch() {
        boolean flag = false;
        if (_notches != null) {
            if (_notches.indexOf(alphabet().toChar(getSettingInt())) >= 0) {
                flag = true;
            }
        }
        return flag;
    }

    /** My notches. */
    private String _notches;

}
