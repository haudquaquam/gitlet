package enigma;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AlphabetTest {

    private Alphabet lowerCaseAlpha = new Alphabet("abcdefghijklmnopqrstuvwxyz");
    private Alphabet emptyAlpha = new Alphabet("");
    private Alphabet randomAlpha = new Alphabet("1234567890hellomynameisRae!");

    @Test
    public void checkSize() {
        assertEquals(26, lowerCaseAlpha.size());
        assertEquals(0, emptyAlpha.size());
        assertEquals(27, randomAlpha.size());
    }

    @Test
    public void checkIndex() {
        assertEquals(4, lowerCaseAlpha.toInt('e'));
        assertEquals(25, lowerCaseAlpha.toInt('z'));
        assertEquals(2, lowerCaseAlpha.toInt('c'));
        assertEquals(11, lowerCaseAlpha.toInt('l'));
        assertEquals(0, lowerCaseAlpha.toInt('a'));
        assertEquals(15, lowerCaseAlpha.toInt('p'));
        assertEquals(-1, lowerCaseAlpha.toInt('5'));

        assertEquals(-1, emptyAlpha.toInt('d'));

        assertEquals(2, randomAlpha.toInt('3'));
        assertEquals(11, randomAlpha.toInt('e'));
        assertEquals(-1, randomAlpha.toInt('K'));
    }

    @Test
    public void checkToChar() {
        assertEquals('h', lowerCaseAlpha.toChar(7));
        assertEquals('z', lowerCaseAlpha.toChar(25));
        assertEquals('q', lowerCaseAlpha.toChar(16));
    }

    @Test
    public void checkContains() {
        assertEquals(true, lowerCaseAlpha.contains('t'));
        assertEquals(true, lowerCaseAlpha.contains('a'));
        assertEquals(true, lowerCaseAlpha.contains('z'));
        assertEquals(false, lowerCaseAlpha.contains('3'));
    }

}
