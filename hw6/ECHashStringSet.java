import java.util.ArrayList;
import java.util.List;

/** A set of String values.
 *  @author Rae Xin
 */
class ECHashStringSet implements StringSet {

    public ECHashStringSet() {
        bucketArray = (ArrayList<String>[]) new ArrayList[defaultNumBuckets];
        for (int i = 0; i < bucketCount; i++) {
            bucketArray[i] = new ArrayList<>();
        }
    }

    @Override
    public void put(String s) {
        int modIndex = whichBucket(s);
        if (!contains(s)) {
            bucketArray[modIndex].add(s);
            totalN++;
        }
        if (totalN / bucketArray.length >= 5) {
            ArrayList<String>[] newList = new ArrayList[bucketArray.length];
            for (int i = 0; i < bucketArray.length / 2; i++) {
                int index = whichBucket(s);
                newList[index] = bucketArray[i];
            }
            for (int k = bucketArray.length / 2; k < bucketArray.length; k++) {
                newList[k] = new ArrayList<>();
            }
            bucketArray = newList;
        }
    }

    private int whichBucket(String s) {
        int modIndex = s.hashCode() % bucketArray.length;
        if (s.hashCode() < 0) {
            modIndex = (s.hashCode() & 0x7fffffff) % bucketArray.length;
        }
        if (bucketArray[modIndex] == null) {
            bucketArray[modIndex] = new ArrayList<String>();
        }
        return modIndex;
    }

    @Override
    public boolean contains(String s) {
        int modIndex = whichBucket(s);
        return bucketArray[modIndex].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < bucketArray.length; i++) {
            if (bucketArray[i] != null) {
                returnList.addAll(bucketArray[i]);
            }
        }
        return returnList;
    }

    private ArrayList<String>[] bucketArray;
    private int totalN;
    private int bucketCount;
    private int defaultNumBuckets = 2;
}
