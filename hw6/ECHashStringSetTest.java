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
        set1.put("charm");
        set1.put("dog");
        set1.put("energy");
        set1.put("fro-yo");
        set1.put("grapple");

        listSet1.add("apple");
        listSet1.add("banana");
        listSet1.add("charm");
        listSet1.add("dog");
        listSet1.add("energy");
        listSet1.add("fro-yo");
        listSet1.add("grapple");

        assertTrue(set1.asList().size() == 7);
    }

    @Test
    public void randomTest() {
        ECHashStringSet set1 = new ECHashStringSet();
        ArrayList<String> correct1 = new ArrayList<>();
        int x = 0;
        while (x < 50) {
            String rand = StringUtils.randomString(10);
            set1.put(rand);
            correct1.add(rand);
            x++;
        }
        set1.put("hello");
        set1.put("hello");

    }

    @Test
    public void compareTest() {
        ECHashStringSet ecSet = new ECHashStringSet();
        BSTStringSet bstSet = new BSTStringSet();

        int x = 0;
        while (x < 10) {
            String rand = StringUtils.randomString(10);
            ecSet.put(rand);
            bstSet.put(rand);
            x++;
        }

        System.out.println("BST List: " + bstSet.asList());
        System.out.println("EC List: " + ecSet.asList());
        assertEquals(bstSet.asList().size(), ecSet.asList().size());
        for (String s : bstSet) {
            assertTrue(ecSet.contains(s));
        }
    }
}