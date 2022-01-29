/** Multidimensional array
 *  @author Zoe Plaxco
 */

public class MultiArr {

    /**
    {{"hello","you","world"} ,{"how","are","you"}} prints:
    Rows: 2
    Columns: 3

    {{1,3,4},{1},{5,6,7,8},{7,9}} prints:
    Rows: 4
    Columns: 4
    */
    public static void printRowAndCol(int[][] arr) {
        int rowCounter = arr.length;
        int columnCounter = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].length > columnCounter) {
                columnCounter = arr[i].length;
            }
        }
    }

    /**
    @param arr: 2d array
    @return maximal value present anywhere in the 2d array
    */
    public static int maxValue(int[][] arr) {
        int largestValue = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > largestValue) {
                    largestValue = arr[i][j];
                }
            }
        }
        return largestValue;
    }

    /**Return an array where each element is the sum of the
    corresponding row of the 2d array*/
    public static int[] allRowSums(int[][] arr) {
        int[] rowSumArray = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int rowSum = 0;
            for (int j = 0; j < arr[i].length; j++) {
                rowSum += arr[i][j];
            }
            rowSumArray[i] = rowSum;
        }
        return rowSumArray;
    }
}
