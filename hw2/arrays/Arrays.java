package arrays;

/* NOTE: The file Arrays/Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2 */

import static arrays.Utils.print;

/** Array utilities.
 *  @author Rae Xin
 */
class Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int length = A.length + B.length;
        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < A.length; j++) {
                result[j] = A[j];
            }
            for (int k = 0; k < B.length; k++) {
                result[A.length + k] = B[k];
            }
        }
        return result;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. If the start + len is out of bounds for our array, you
     *  can return null.
     *  Example: if A is [0, 1, 2, 3] and start is 1 and len is 2, the
     *  result should be [0, 3]. */
    static int[] remove(int[] A, int start, int len) {
        if (start + len <= A.length) {
            int[] result = new int[A.length - len];
            int resultIndex = 0;
            for (int i = 0; i < A.length; i++) {
                if (i < start || i > (start + len - 1)) {
                    result[resultIndex] = A[i];
                    resultIndex++;
                }
            }
            return result;
        }
        else if (len == A.length) {
            int[] result = {};
            return result;
        }
        else {
            return null;
        }
    }
}
