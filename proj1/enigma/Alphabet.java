package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.error;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Rae Xin
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {

        for (int i = 0; i < chars.length(); i++) {
            _alphabetHashMap.put(i, chars.charAt(i));
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabetHashMap.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabetHashMap.containsValue(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= _alphabetHashMap.size()) {
            throw error("Index %d is not in bounds of this alphabet!", index);
        }
        return _alphabetHashMap.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        int value = -1;
        int index = 0;
        while (index < _alphabetHashMap.size()) {
            if (_alphabetHashMap.get(index) == ch) {
                value = index;
                index = _alphabetHashMap.size();
            } else {
                index++;
            }
        }
        return value;
    }

    /** Characters of the alphabet mapped to their indices. */
    private HashMap<Integer, Character> _alphabetHashMap = new HashMap<>();

}
