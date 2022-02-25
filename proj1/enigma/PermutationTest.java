package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Rae Xin
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }


    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", simpleAlpha);
        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('D', p.invert('B'));
        assertEquals('C', p.invert('D'));
    }

    public Alphabet simpleAlpha = new Alphabet("ABCDEFGH");
    public Permutation simplePerm = new Permutation("(ABCD)(EFGH)", simpleAlpha);
    public Alphabet shortAlpha = new Alphabet("XYZ");
    public Permutation shortPerm = new Permutation("(XZ)", shortAlpha);
    public Alphabet emptyAlpha = new Alphabet("");
    public Permutation emptyPerm = new Permutation("", emptyAlpha);
    public Alphabet bigAlpha = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
    public Permutation bigPerm = new Permutation("(AaBbCcDdEe)  (FGHI) (JKLmnopq)(RSTUV12345) (0)", bigAlpha);
    public Alphabet whiteSpaceAlpha = new Alphabet("ABCDEFGHIJK");
    public Permutation whiteSpacePerm = new Permutation("  (AB) (DE)  (F)    (G) ", whiteSpaceAlpha);
    public Alphabet derangementCheckAlpha = new Alphabet("ABCD");
    public Permutation derangementCheckPerm = new Permutation("(A) (B) (C) (D)", derangementCheckAlpha);

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabetPermute() {
        simplePerm.permute('i');
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabetInvert() {
        simplePerm.invert('9');
    }

    @Test
    public void checkSize() {
        assertEquals(8, simplePerm.size());
        assertEquals(3, shortPerm.size());
        assertEquals(0, emptyPerm.size());
        assertEquals(62, bigPerm.size());
        assertEquals(11, whiteSpacePerm.size());
        assertEquals(4, derangementCheckPerm.size());
    }

    @Test
    public void checkPermute() {
        assertEquals(1, simplePerm.permute(0));
        assertEquals('C', simplePerm.permute('B'));
        assertEquals(0, simplePerm.permute(3));
        assertEquals('A', simplePerm.permute('D'));
        assertEquals(6, simplePerm.permute(5));
        assertEquals('G', simplePerm.permute('F'));
        assertEquals(4, simplePerm.permute(7));
        assertEquals('E', simplePerm.permute('H'));
        assertEquals(1, shortPerm.permute(4));
        assertEquals('Y', shortPerm.permute('Y'));

        assertEquals(2, shortPerm.permute(-666));
        assertEquals(1, shortPerm.permute(-1001));

        assertEquals(61, bigPerm.permute(61));
        assertEquals('b', bigPerm.permute('B'));
        assertEquals(26, bigPerm.permute(0));
        assertEquals('0', bigPerm.permute('0'));

        assertEquals(4, whiteSpacePerm.permute(3));
        assertEquals('E', whiteSpacePerm.permute('D'));
    }

    @Test
    public void checkInvert() {
        assertEquals(0, simplePerm.invert(1));
        assertEquals('A', simplePerm.invert('B'));
        assertEquals(3, simplePerm.invert(0));
        assertEquals('D', simplePerm.invert('A'));
        assertEquals(5, simplePerm.invert(6));
        assertEquals('F', simplePerm.invert('G'));
        assertEquals(7, simplePerm.invert(4));
        assertEquals('H', simplePerm.invert('E'));
        assertEquals(1, shortPerm.invert(1));
        assertEquals('Y', shortPerm.invert('Y'));

        assertEquals(3, simplePerm.invert(24));
        assertEquals(3, simplePerm.invert(-888));
        assertEquals(2, shortPerm.invert(-666));

        assertEquals(61, bigPerm.invert(61));
        assertEquals('B', bigPerm.invert('b'));
        assertEquals(0, bigPerm.invert(26));
        assertEquals('0', bigPerm.invert('0'));

        assertEquals(3, whiteSpacePerm.invert(4));
        assertEquals('D', whiteSpacePerm.invert('E'));


    }

    @Test
    public void checkDerangement() {
        assertEquals(true, simplePerm.derangement());
        assertEquals(false, shortPerm.derangement());
        assertEquals(true, emptyPerm.derangement());
        assertEquals(false, bigPerm.derangement());
        assertEquals(false, whiteSpacePerm.derangement());
        assertEquals(false, derangementCheckPerm.derangement());
    }
}


