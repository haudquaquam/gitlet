package enigma;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AlphabetTest {

    private Alphabet lowerAlpha = new Alphabet("abcdefghijklmnopqrstuvwxyz");
    private Alphabet emptyAlpha = new Alphabet("");
    private Alphabet randomAlpha = new Alphabet("1234567890hellomynameisRae!");

    @Test
    public void checkSize() {
        assertEquals(26, lowerAlpha.size());
        assertEquals(0, emptyAlpha.size());
        assertEquals(27, randomAlpha.size());
    }

    @Test
    public void checkIndex() {
        assertEquals(4, lowerAlpha.toInt('e'));
        assertEquals(25, lowerAlpha.toInt('z'));
        assertEquals(2, lowerAlpha.toInt('c'));
        assertEquals(11, lowerAlpha.toInt('l'));
        assertEquals(0, lowerAlpha.toInt('a'));
        assertEquals(15, lowerAlpha.toInt('p'));
        assertEquals(-1, lowerAlpha.toInt('5'));

        assertEquals(-1, emptyAlpha.toInt('d'));

        assertEquals(2, randomAlpha.toInt('3'));
        assertEquals(11, randomAlpha.toInt('e'));
        assertEquals(-1, randomAlpha.toInt('K'));
    }

    @Test
    public void checkToChar() {
        assertEquals('h', lowerAlpha.toChar(7));
        assertEquals('z', lowerAlpha.toChar(25));
        assertEquals('q', lowerAlpha.toChar(16));

        assertEquals('!', randomAlpha.toChar(26));
        assertEquals('h', randomAlpha.toChar(10));
    }

    @Test
    public void checkContains() {
        assertEquals(true, lowerAlpha.contains('t'));
        assertEquals(true, lowerAlpha.contains('a'));
        assertEquals(true, lowerAlpha.contains('z'));
        assertEquals(false, lowerAlpha.contains('3'));

        assertEquals(false, emptyAlpha.contains('3'));

        assertEquals(false, randomAlpha.contains('?'));
        assertEquals(true, randomAlpha.contains('R'));
    }

}
