import org.junit.Test;

import static org.junit.Assert.*;

public class SumTest {
    @Test
    public void findSum() {
        int[] a = new int[] {2, 3, 4, 5, 6, 7};
        int[] b = new int[] {2, 3, 4, 5, 6, 7};
        assertTrue(Sum.sumsTo(a, b, 4));
        assertFalse(Sum.sumsTo(a, b, 19));
    }
}
