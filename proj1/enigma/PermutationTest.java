package enigma;

import org.checkerframework.checker.units.qual.A;
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
        Permutation p = new Permutation("(BACD)", alpha1);
        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('D', p.invert('B'));
        assertEquals('C', p.invert('D'));
    }

    public Alphabet alpha1 = new Alphabet("ABCDEFGH");
    public Permutation perm1 = new Permutation("(ABCD)(EFGH)", alpha1);
    public Alphabet alpha2 = new Alphabet("XYZ");
    public Permutation perm2 = new Permutation("(XZ)", alpha2);
    public Alphabet emptyAlpha = new Alphabet("");
    public Permutation permEmpty = new Permutation("", emptyAlpha);
    public Alphabet alphaBig = new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
    public Permutation permBig = new Permutation("(AaBbCcDdEe)  (FGHI) (JKLmnopq)(RSTUV12345) (0)", alphaBig);
    public Alphabet whiteSpaceAlpha = new Alphabet("ABCDEFGHIJK");
    public Permutation whiteSpacePerm = new Permutation("  (AB) (DE)  (F)       (G) ", whiteSpaceAlpha);

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.invert('F');
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabetPermute() {
        perm1.permute('0');
    }

    @Test
    public void checkSize() {
        assertEquals(8, perm1.size());
        assertEquals(3, perm2.size());
        assertEquals(0, permEmpty.size());
        assertEquals(62, permBig.size());
        assertEquals(11, whiteSpacePerm.size());
    }

    @Test
    public void checkPermute() {
        assertEquals(1, perm1.permute(0));
        assertEquals('B', perm1.permute('A'));
        assertEquals(0, perm1.permute(3));
        assertEquals('A', perm1.permute('D'));
        assertEquals(6, perm1.permute(5));
        assertEquals('G', perm1.permute('F'));
        assertEquals(4, perm1.permute(7));
        assertEquals('E', perm1.permute('H'));
        assertEquals(1, perm2.permute(4));
        assertEquals('Y', perm2.permute('Y'));

        assertEquals(2, perm2.permute(-999));
        assertEquals(1, perm2.permute(-1001));

        assertEquals(61, permBig.permute(61));
        assertEquals('b', permBig.permute('B'));
        assertEquals(26, permBig.permute(0));
        assertEquals('0', permBig.permute('0'));

        assertEquals(4, whiteSpacePerm.permute(3));
        assertEquals('E', whiteSpacePerm.permute('D'));
    }

    @Test
    public void checkInvert() {
        assertEquals(0, perm1.invert(1));
        assertEquals('A', perm1.invert('B'));
        assertEquals(3, perm1.invert(0));
        assertEquals('D', perm1.invert('A'));
        assertEquals(5, perm1.invert(6));
        assertEquals('F', perm1.invert('G'));
        assertEquals(7, perm1.invert(4));
        assertEquals('H', perm1.invert('E'));
        assertEquals(1, perm2.invert(1));
        assertEquals('Y', perm2.invert('Y'));

        assertEquals(3, perm1.invert(8));
        assertEquals(3, perm1.invert(-64));
        assertEquals(2, perm2.invert(-999));

        assertEquals(61, permBig.invert(61));
        assertEquals('B', permBig.invert('b'));
        assertEquals(0, permBig.invert(26));
        assertEquals('0', permBig.invert('0'));

        assertEquals(3, whiteSpacePerm.invert(4));
        assertEquals('D', whiteSpacePerm.invert('E'));


    }

    /*@Test
    public void alphabetTest() {
        assertEquals(new Alphabet("ABCDEFGH"), perm1.alphabet());
        assertEquals(new Alphabet("XYZ"), perm2.alphabet());
        assertEquals(new Alphabet(""), permEmpty.alphabet());
    }*/

    @Test
    public void checkDerangement() {
        assertEquals(true, perm1.derangement());
        assertEquals(false, perm2.derangement());
        assertEquals(true, permEmpty.derangement());
        assertEquals(false, permBig.derangement());
        assertEquals(false, whiteSpacePerm.derangement());
    }
}


