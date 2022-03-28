import java.util.ArrayList;
import java.util.List;

/** A set of String values.
 *  @author Rae Xin
 */
class ECHashStringSet implements StringSet {

    @Override
    public void put(String s) {
        int modIndex = s.hashCode() % bucketArray.length;
        if (s.hashCode() < 0) {
            modIndex = (s.hashCode() & 0x7fffffff) % bucketArray.length;
        }
        if (bucketArray[modIndex] == null) {
            bucketArray[modIndex] = new ArrayList();
        }
        bucketArray[modIndex].add(s);
        totalN++;
        if (totalN / bucketArray.length >= 5) {
            ArrayList<String>[] newList = new ArrayList[bucketArray.length * 2];
            for (int i = 0; i < bucketArray.length; i++) {
                newList[i] = bucketArray[i];
            }
            bucketArray = newList;
        }
    }

    @Override
    public boolean contains(String s) {
        int modIndex = s.hashCode() % bucketArray.length;
        return bucketArray[modIndex].contains(s);
    }

    @Override
    public List<String> asList() {
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < bucketArray.length; i++) {
            if (bucketArray[i] != null) {
                for (String s : bucketArray[i]) {
                    returnList.add(s);
                }
            }
        }
        return returnList;
    }

    private ArrayList<String>[] bucketArray = new ArrayList[1];
    private int totalN;
}
