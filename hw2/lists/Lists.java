package lists;

/* NOTE: The file Utils.java contains some functions that may be useful
 * in testing your answers. */

/** HW #2, Problem #1. */

import image.In;

/** List problem.
 *  @author
 */
class Lists {


    /* B. */
    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntListList naturalRuns(IntList L) {

        // Feel free to ignore this skeleton and start fresh
        // if that's more your vibe

        IntListList result = new IntListList();
        IntListList resultReturn = result;
        IntList runList = new IntList();
        IntList runListPointer = runList;
        while (L.tail != null) {
            if (L.head < L.tail.head) {
                runList.head = L.head;
                runList.tail = new IntList();
                runList = runList.tail;
                L = L.tail;
            } else {
                runList.head = L.head;
                result.head = runListPointer; // current result spot is set to the IntList, runList
                result.tail = new IntListList(); result = result.tail; // current result spot is set to the next result spot // give an empty IntListList to the result.tail so that it is not null
                runList = new IntList();
                L = L.tail;
                // runList = L; // set runList equal to L, the remaining original list
                runListPointer = runList; // reset runListPointer to runList, so we can set the result.head to runListPointer when done with next run
            }
        }
        runList.head = L.head;
        result.head = runListPointer;

        System.out.println("runList: " + runList + " L: " + L + " result: " + result + " runListPointer: " + runListPointer);
        return resultReturn;
    }

    /** Same as above, but a recursive version.
     *
     *  If you choose to go with the recursive skeleton, make sure to change the
     *  name from naturalRunsRecursive to naturalRuns, and delete the iterative
     *  skeleton. Otherwise, our autograder will grade the iterative version above.
     * */
    static IntListList naturalRunsRecursive(IntList L) {
        if (L == null) {
            return null; // Should you replace me?
        } else {
            // FIXME: Add some lines here...
            //
            //
            // return new IntListList(L, rest); <- You might want this return statement...
            //                                    but how should you define "rest"?
            return null; // FIXME: REPLACE ME!
        }
    }

    /** Recursive helper method, if you'd like.
     *
     *  Assuming L is not null, returns the last element of L in which
     *  the value of L.head increases from the previous element (the
     *  end of the list if L is entirely in strictly ascending order).  */
    private static IntList endOfRun(IntList L) {
        while (L.tail != null && L.tail.head > L.head) {
            L = L.tail;
        }
        return L;
    }
}
