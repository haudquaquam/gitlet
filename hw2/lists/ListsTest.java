package lists;

import org.junit.Test;

import static lists.Lists.naturalRuns;
import static org.junit.Assert.*;

/** FIXME
 *
 *  @author FIXME
 */

public class ListsTest {

    @Test
    public void basicRunsTest() {
        IntList input = IntList.list(1, 2, 3, 1, 2);
        IntList run1 = IntList.list(1, 2, 3);
        IntList run2 = IntList.list(1, 2);
        IntListList result = IntListList.list(run1, run2);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void fourRunsTest() {
        IntList input = IntList.list(1, 2, 3, 1, 2, 1, 1, 2, 3);
        IntList run1 = IntList.list(1, 2, 3);
        IntList run2 = IntList.list(1, 2);
        IntList run3 = IntList.list(1);
        IntList run4 = IntList.list(1, 2, 3);
        IntListList result = IntListList.list(run1, run2, run3, run4);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void twoOneTwoTest() {
        IntList input = IntList.list(2, 1, 2);
        IntList run1 = IntList.list(2);
        IntList run2 = IntList.list(1, 2);
        IntListList result = IntListList.list(run1, run2);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void oneListTest() {
        IntList input = IntList.list(1);
        IntList run1 = IntList.list(1);
        IntListList result = IntListList.list(run1);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void oneOneOneTest() {
        IntList input = IntList.list(1, 1, 1);
        IntList run1 = IntList.list(1);
        IntList run2 = IntList.list(1);
        IntList run3 = IntList.list(1);
        IntListList result = IntListList.list(run1, run2, run3);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void lastNumTest() {
        IntList input = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 2);
        IntList run1 = IntList.list(1, 3, 7);
        IntList run2 = IntList.list(5);
        IntList run3 = IntList.list(4, 6, 9, 10);
        IntList run4 = IntList.list(10);
        IntList run5 = IntList.list(2);
        IntListList result = IntListList.list(run1, run2, run3, run4, run5);
        assertEquals(result, naturalRuns(input));
    }

    @Test
    public void finalBossTest() {
        IntList input = IntList.list(1, 3, 7, 5, 4, 6, 9, 10, 10, 11);
        IntList run1 = IntList.list(1, 3, 7);
        IntList run2 = IntList.list(5);
        IntList run3 = IntList.list(4, 6, 9, 10);
        IntList run4 = IntList.list(10, 11);
        IntListList result = IntListList.list(run1, run2, run3, run4);
        assertEquals(result, naturalRuns(input));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
