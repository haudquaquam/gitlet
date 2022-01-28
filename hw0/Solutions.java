/** Solutions to the HW0 Java101 exercises.
 *  @author Allyson Park and [INSERT YOUR NAME HERE]
 */
public class Solutions {

    /** Returns whether or not the input x is even.
     */
    public static boolean isEven(int x) {
        // TODO: Your code here. Replace the following return statement.
        if (x % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public static int max(int[] a) {
        int maximum = a[0];
        int i = 1;
        while ((i < a.length) && (a[i] > maximum)) {
            maximum = a[i];
            i++;
        }
        return maximum;
    }

    public static boolean wordBank(String word, String[] bank) {
        boolean flag = false;
        for (int i = 0; i < bank.length; i++) {
            if (word.equals(bank[i])) {
                flag = true;
            }
        }
        return flag;
    }

    public static boolean threeSum(int[] a) {
        boolean flag = false;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < a.length; k++) {
                    if ((a[i] + a[j] + a[k]) == 0) {
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }


    // TODO: Fill in the method signatures for the other exercises
    // Your methods should be static for this HW. DO NOT worry about what this means.
    // Note that "static" is not necessarily a default, it just happens to be what
    // we want for THIS homework. In the future, do not assume all methods should be
    // static.

}
