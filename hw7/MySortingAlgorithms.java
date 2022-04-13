import java.util.Arrays;
import java.util.regex.Pattern;

import static java.lang.Math.max;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting 
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            int index = 1;
            while (index < k) {
                int small = max(index - 1, 0);
                if (array[index] < array[small]) {
                    int tempVal = array[index];
                    array[index] = array[small];
                    array[small] = tempVal;
                    index = small;
                } else {
                    index++;
                }
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            for (int i = 0; i < k; i++) {
                int min = array[i];
                int minIndex = i;
                for (int j = i; j < k; j++) {
                    if (array[j] < min) {
                        min = array[j];
                        minIndex = j;
                    }
                }
                int tempVal = min;
                array[minIndex] = array[i];
                array[i] = tempVal;
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k > 1 && array.length >= 1) {
                int mid = k / 2;
                int[] left = Arrays.copyOfRange(array, 0, mid);
                int[] right = Arrays.copyOfRange(array, mid, k);
                merge(array, left, right, k);
            } else {
                System.out.println("array passed into sort func is empty");
            }
        }

        public void merge(int[] original, int[] array1, int[] array2, int k) {
            int index1 = 0;
            int index2 = 0;
            int len1 = array1.length;
            int len2 = array2.length;
            int[] newArray = new int[original.length];
            System.arraycopy(original, 0, newArray, 0, original.length);
            if (len1 > 1) sort(array1, len1);
            if (len2 > 1) sort(array2, len2);
            while (index1 < len1 && index2 < len2) {
                if (array1[index1] > array2[index2]) {
                    newArray[index1 + index2] = array2[index2];
                    index2++;
                } else {
                    newArray[index1 + index2] = array1[index1];
                    index1++;
                }
            }
            if (index1 >= len1) {
                while (index2 < len2) {
                    newArray[index1 + index2] = array2[index2];
                    index2++;
                }
            } else if (index2 >= len2) {
                while (index1 < len1) {
                    newArray[index1 + index2] = array1[index1];
                    index1++;
                }
            }

            System.arraycopy(newArray, 0, original, 0, original.length);
        }


        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME: to be implemented
        }

        // may want to add additional methods

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            /*String[] binArray = new String[a.length];
            int maxStringLen = 0;
            for (int i = 0; i < k; i++) {
                binArray[i] = Integer.toBinaryString(a[i]);
                if (binArray[i].length() > maxStringLen) {
                    maxStringLen = binArray[i].length();
                }
            }
            for (int i = 0; i < k; i++) {
                while (binArray[i].length() < maxStringLen) {
                    binArray[i] = "0" + binArray[i];
                }

            }*/
            int max = 0;
            for (int i = 0; i < k; i++) {
                max = Math.max(max, a[i]);
            }
            int numDigits = 0;
            int tens = 1;
            while (max >= tens) {
                numDigits++;
                tens *= 10;
            }
            int places = 1;
            while (numDigits > 0) {
                countSort(a, places, k);
                places *= 10;
                numDigits--;
            }

        }

        private void countSort(int[] a, int places, int k) {
            int range = 10;
            int[] frequency = new int[range];
            int[] sorted = new int[a.length];

            for (int i = 0; i < k; i++) {
                int digit = (a[i] / places) % range;
                frequency[digit]++;
            }

            for (int i = 1; i < range; i++) {
                frequency[i] += frequency[i - 1];
            }

            for (int i = k - 1; i >= 0; i--) {
                int digit = (a[i] / places) % range;
                sorted[frequency[digit] - 1] = a[i];
                frequency[digit]--;
            }

            System.arraycopy(sorted, 0, a, 0, k);
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            // FIXME
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    private static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

}
