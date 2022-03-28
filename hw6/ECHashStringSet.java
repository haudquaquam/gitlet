import java.util.ArrayList;
import java.util.List;

/** A set of String values.
 *  @author Rae Xin
 */
class ECHashStringSet implements StringSet {

    @Override
    public void put(String s) {
        int modIndex = s.hashCode() % bucketCount;
        if (s.hashCode() < 0) {
            modIndex = (s.hashCode() & 0x7fffffff) % bucketCount;
        }
        if (bucketArray[modIndex] == null) {
            bucketArray[modIndex] = new ArrayList<String>();
        }
        bucketArray[modIndex].add(s);
        totalN++;
        if (totalN / bucketCount >= 5) {
            ArrayList<String>[] newList = new ArrayList[bucketCount * 2];
            for (int i = 0; i < bucketCount; i++) {
                newList[i] = bucketArray[i];
            }
            bucketArray = newList;
        }
    }

    @Override
    public boolean contains(String s) {
        int modIndex = s.hashCode() % bucketCount;
        return bucketArray[modIndex] != null && bucketArray[modIndex].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < bucketCount; i++) {
            if (bucketArray[i] != null) {
                returnList.addAll(bucketArray[i]);
            }
        }
        return returnList;
    }

    private ArrayList<String>[] bucketArray = new ArrayList[1];
    private int totalN;
    private int bucketCount = 1;
}
