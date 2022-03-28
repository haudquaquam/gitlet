import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author Rae Xin
 */
public class BSTStringSetTest  {
    // FIXME: Add your own tests for your BST StringSet

    /*BSTStringSet set = new BSTStringSet();
    ArrayList listSet = new ArrayList<String>();

    public void setListSet(ArrayList listSet) {
        this.listSet = listSet;
    }

    public void setSet(BSTStringSet set) {
        this.set = set;
    }*/

    @Test
    public void overAllStringTest() {
        BSTStringSet set1 = new BSTStringSet();
        ArrayList listSet1 = new ArrayList<String>();
        set1.put("apple");
        set1.put("banana");
        set1.put("dog");
        set1.put("charm");
        set1.put("grapple");
        set1.put("energy");
        set1.put("fro-yo");

        listSet1.add("apple");
        listSet1.add("banana");
        listSet1.add("charm");
        listSet1.add("dog");
        listSet1.add("energy");
        listSet1.add("fro-yo");
        listSet1.add("grapple");

        assertTrue(set1.asList().size() == 7);
        assertTrue(set1.asList().get(4) == "energy");
        assertTrue(set1.asList().equals(listSet1));
    }
}
