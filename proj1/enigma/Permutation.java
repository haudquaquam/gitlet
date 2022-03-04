package enigma;

import static enigma.EnigmaException.*;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Rae Xin
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        Pattern regex = Pattern.compile("\\((.*?)\\)");
        Matcher regexMatch = regex.matcher(cycles);

        while (regexMatch.find()) {
            _cycleList.add(regexMatch.group(1));
        }

        for (int i = 0; i < _cycleList.size(); i++) {

            String cycleString = _cycleList.get(i);
            ArrayList<Integer> currentCycleIndexList = new ArrayList<>();

            for (int k = 0; k < cycleString.length(); k++) {
                if (_alphabet.contains(cycleString.charAt(k))) {
                    int cycle = _alphabet.toInt(cycleString.charAt(k));
                    currentCycleIndexList.add(cycle);
                } else {
                    throw error("Cycle has letters not in alphabet");
                }
            }

            _cycleIndexList.add(currentCycleIndexList);
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        ArrayList<Integer> currentCycleIndexList = new ArrayList<>();
        for (int i = 0; i < cycle.length(); i++) {
            if (!(_alphabet.contains(cycle.charAt(i)))) {
                throw error("Trying to add invalid cycle!");
            }
            currentCycleIndexList.add(_alphabet.toInt(cycle.charAt(i)));
        }
        _cycleIndexList.add(currentCycleIndexList);
        _cycleList.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    ArrayList<String> permutationArrayList() {
        return _cycleList;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        p = wrap(p);

        for (int i = 0; i < _cycleIndexList.size(); i++) {
            ArrayList<Integer> currentCycleIndexList = _cycleIndexList.get(i);
            for (int k = 0; k < currentCycleIndexList.size(); k++) {
                if (currentCycleIndexList.get(k) == p) {
                    if (k < currentCycleIndexList.size() - 1) {
                        return currentCycleIndexList.get(k + 1);
                    } else {
                        return currentCycleIndexList.get(0);
                    }
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        c = wrap(c);

        for (int i = 0; i < _cycleIndexList.size(); i++) {
            ArrayList<Integer> currentCycleIndexList = _cycleIndexList.get(i);
            for (int k = 0; k < currentCycleIndexList.size(); k++) {
                if (currentCycleIndexList.get(k) == c) {
                    if (k > 0) {
                        return currentCycleIndexList.get(k - 1);
                    } else {
                        int lastIndex = currentCycleIndexList.size() - 1;
                        return currentCycleIndexList.get(lastIndex);
                    }
                }
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {

        if (!_alphabet.contains(p)) {
            throw new EnigmaException("Character \"%p\" is not in alphabet!");
        }

        for (int i = 0; i < _cycleList.size(); i++) {
            String currentCycleString = _cycleList.get(i);
            int indexOfP = currentCycleString.indexOf(p);
            if (indexOfP >= 0) {
                if (indexOfP == currentCycleString.length() - 1) {
                    return currentCycleString.charAt(0);
                } else {
                    return currentCycleString.charAt(indexOfP + 1);
                }
            }
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {

        if (!_alphabet.contains(c)) {
            throw new EnigmaException("Character \"%c\" is not in alphabet!");
        }

        for (int i = 0; i < _cycleList.size(); i++) {
            String currentCycle = _cycleList.get(i);
            int indexOfC = currentCycle.indexOf(c);
            if (indexOfC >= 0) {
                if (indexOfC == 0) {
                    return currentCycle.charAt(currentCycle.length() - 1);
                } else {
                    return currentCycle.charAt(indexOfC - 1);
                }
            }
        }
        return c;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int alphaSum = 0;
        for (int i = 0; i < _cycleList.size(); i++) {
            if (_cycleList.get(i).length() == 1) {
                return false;
            }
            alphaSum += _cycleList.get(i).length();
        }
        return (alphaSum == _alphabet.size());
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** List of cycles. */
    private ArrayList<String> _cycleList = new ArrayList<>();

    /** List of cycle lists. */
    private ArrayList<ArrayList<Integer>> _cycleIndexList = new ArrayList<>();

}
