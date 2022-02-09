package image;

import org.junit.Test;

import static image.MatrixUtils.*;
import static org.junit.Assert.*;

/** FIXME
 *  @author FIXME
 */

public class MatrixUtilsTest {

    @Test
    public void simpleMatrixTest() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000},
                         {1000000, 75990, 30003, 1000000},
                         {1000000, 30002, 103046, 1000000},
                         {1000000, 29515, 38273, 1000000},
                         {1000000, 73403, 35399, 1000000},
                         {1000000, 1000000, 1000000, 1000000}};
        double[][] output = {{1000000, 1000000, 1000000, 1000000},
                         {2000000, 1075990, 1030003, 2000000},
                         {2075990, 1060005, 1133049, 2030003},
                         {2060005, 1089520, 1098278, 2133049},
                         {2089520, 1162923, 1124919, 2098278},
                         {2162923, 2124919, 2124919, 2124919}};
        assertArrayEquals(accumulateVertical(input), (output));
    }

    @Test
    public void emptyMatrixTest() {
        double[][] input = {{}};
        double[][] output = {{}};
        assertArrayEquals(accumulateVertical(input), output);
    }

    @Test
    public void oneLineMatrixTest() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000, 1000000}};
        double[][] output = {{1000000, 1000000, 1000000, 1000000, 1000000}};
        assertArrayEquals(accumulateVertical(input), output);
    }

    @Test
    public void horizontalCopyTest() {
        double[][] input = {{1, 2}, {3, 4}, {5, 6}};
        double[][] output = {{1, 3, 5}, {2, 4, 6}};
        assertArrayEquals(output, createHorizontalCopy(input));
    }

    @Test
    public void bigHorizontalCopyTest() {
        double[][] input =
                {{1000000, 1000000, 1000000, 1000000},
                {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000},
                {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000},
                {1000000, 1000000, 1000000, 1000000}};
        double[][] output =
                {{1000000, 1000000, 1000000, 1000000, 1000000, 1000000},
                {1000000, 75990, 30002, 29515, 73403, 1000000},
                {1000000, 30003, 103046, 38273, 35399, 1000000},
                {1000000, 1000000, 1000000, 1000000, 1000000, 1000000}};
        assertArrayEquals(output, createHorizontalCopy(input));
    }

    @Test
    public void horizontalAccumulateTest() {
        double[][] input =
                {{1000000, 1000000, 1000000, 1000000},
                {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000},
                {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000},
                {1000000, 1000000, 1000000, 1000000}};
        double[][] output =
                {{1000000, 2000000, 2075990, 2060005},
                {1000000, 1075990, 1060005, 2060005},
                {1000000, 1030002, 1132561, 2060005},
                {1000000, 1029515, 1067788, 2064914},
                {1000000, 1073403, 1064914, 2064914},
                {1000000, 2000000, 2073403, 2064914}};
        assertArrayEquals(output, accumulate(input, MatrixUtils.Orientation.HORIZONTAL));
    }

    @Test
    public void verticalAccumulateTest() {
        double[][] input = {{1000000, 1000000, 1000000, 1000000},
                {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000},
                {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000},
                {1000000, 1000000, 1000000, 1000000}};
        double[][] output = {{1000000, 1000000, 1000000, 1000000},
                {2000000, 1075990, 1030003, 2000000},
                {2075990, 1060005, 1133049, 2030003},
                {2060005, 1089520, 1098278, 2133049},
                {2089520, 1162923, 1124919, 2098278},
                {2162923, 2124919, 2124919, 2124919}};
        assertArrayEquals(output, accumulate(input, Orientation.VERTICAL));
    }

    @Test
    public void emptyAccumulateTest() {
        double[][] input = {};
        double[][] output = {};
        assertArrayEquals(output, accumulate(input, Orientation.HORIZONTAL));
        assertArrayEquals(output, accumulate(output, Orientation.VERTICAL));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
