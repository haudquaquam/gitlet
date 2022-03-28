import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test of a BST-based String Set.
 * @author
 */
public class ECHashStringSetTest  {
    // FIXME: Add your own tests for your ECHashStringSetTest

    @Test
    public void overAllStringTest() {
        ECHashStringSet set1 = new ECHashStringSet();
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
        System.out.println(set1.asList());
    }
}
