import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;

public class MultiArrTest {

    @Test
    public void testMaxValue() {
        int[][] arr1 = {{1}, {2, 3}, {4, 5}};
        int[][] arr2 = {{1}, {1}, {1}};
        int[][] arr3 = {{}, {}, {}};
        int[][] arr4 = {{-4, -2}, {}, {-3, -5}};


        assertEquals(MultiArr.maxValue(arr1), 5);
        assertEquals(MultiArr.maxValue(arr2), 1);
        assertEquals(MultiArr.maxValue(arr3), 0);
        assertEquals(MultiArr.maxValue(arr4), 0);

    }

    @Test
    public void testAllRowSums() {
        int[][] arr1 = {{1}, {2, 3}, {4, 5}};
        int[][] arr2 = {{1}, {1}, {1}};
        int[][] arr3 = {{}, {}, {}};
        int[][] arr4 = {{-4, -2}, {}, {-3, -5}};

        int[] arrayCheck1 = {1, 5, 9};
        int[] arrayCheck2 = {1, 1, 1};
        int[] arrayCheck3 = {0, 0, 0};
        int[] arrayCheck4 = {-6, 0, -8};


        assertTrue(Arrays.equals(MultiArr.allRowSums(arr1), arrayCheck1));
        assertTrue(Arrays.equals(MultiArr.allRowSums(arr2), arrayCheck2));
        assertTrue(Arrays.equals(MultiArr.allRowSums(arr3), arrayCheck3));
        assertTrue(Arrays.equals(MultiArr.allRowSums(arr4), arrayCheck4));

    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        System.exit(ucb.junit.textui.runClasses(MultiArrTest.class));
    }
}
