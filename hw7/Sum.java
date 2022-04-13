import java.util.Arrays;

/** HW #7, Two-sum problem.
 * @author Rae Xin
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        int[] arr = B;
        int[] oth = A;
        if (A.length < B.length) {
            arr = A;
            oth = B;
        }
        for (int i = 0; i < arr.length; i++) {
            int diff = m - arr[i];
            if (Arrays.binarySearch(oth, diff) >= 0) {
                return true;
            }
        }
        return false;
    }

}
