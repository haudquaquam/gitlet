package arrays;

import org.junit.Test;

import static arrays.Arrays.catenate;
import static arrays.Arrays.remove;
import static org.junit.Assert.*;

/** Array utilities testing.
 *  @author Rae Xin
 */

public class ArraysTest {

    @Test
    public void simpleCatenateTest() {
        int[] array1 = {1, 2};
        int[] array2 = {3, 4};
        int[] result = {1, 2, 3, 4};
        assertTrue(java.util.Arrays.equals(result, catenate(array1, array2)));
    }

    @Test
    public void emptyCatenateTest() {
        int[] array1 = {};
        int[] array2 = {};
        int[] result = {};
        assertTrue(java.util.Arrays.equals(result, catenate(array1, array2)));
    }

    @Test
    public void oneEmptyCatenateTest() {
        int[] array1 = {1};
        int[] array2 = {};
        int[] result = {1};
        assertTrue(java.util.Arrays.equals(result, catenate(array1, array2)));
    }

    @Test
    public void bigCatenateTest() {
        int[] array1 = {1, 5, 7, 2, 4, 6, 9};
        int[] array2 = {3, 4, 5, 6, 7, 1, 2, 3, 0};
        int[] result = {1, 5, 7, 2, 4, 6, 9, 3, 4, 5, 6, 7, 1, 2, 3, 0};
        assertTrue(java.util.Arrays.equals(result, catenate(array1, array2)));
    }

    @Test
    public void simpleRemoveTest() {
        int[] array1 = {1, 3, 5, 7, 2, 4, 6};
        int[] result = {5, 7, 2, 4, 6};
        assertTrue(java.util.Arrays.equals(result, Arrays.remove(array1, 0, 2)));
    }

    @Test
    public void outOfBoundsRemoveTest() {
        int[] array1 = {1, 3, 5, 7, 2, 4, 6};
        int[] result = null;
        assertTrue(java.util.Arrays.equals(result, Arrays.remove(array1, 0, 20)));
    }

    @Test
    public void fullRemoveTest() {
        int[] array1 = {1, 3, 5, 7, 2, 4, 6};
        int[] result = {};
        assertTrue(java.util.Arrays.equals(result, Arrays.remove(array1, 0, 7)));
    }

    @Test
    public void leaveOneRemoveTest() {
        int[] array1 = {1, 3, 5, 7, 2, 4, 6};
        int[] result = {1};
        assertTrue(java.util.Arrays.equals(result, Arrays.remove(array1, 1, 6)));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
