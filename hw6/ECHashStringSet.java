import java.util.ArrayList;
import java.util.List;

/** A set of String values.
 *  @author Rae Xin
 */
class ECHashStringSet implements StringSet {

    public ECHashStringSet() {
        _bucketArray = (ArrayList<String>[]) new ArrayList[_defaultNumBuckets];
        _bucketCount = _bucketArray.length;
        for (int i = 0; i < _bucketCount; i++) {
            _bucketArray[i] = new ArrayList<>();
        }
    }

    @Override
    public void put(String s) {
        int modIndex = ((s.hashCode() % _bucketCount) + _bucketCount) % _bucketCount;
        /*if (modIndex < 0) {
            modIndex = modIndex % 0x7fffffff % _bucketCount;
        }*/

        /*if (!contains(s)) {

        }*/
        while (modIndex > _bucketCount) {
            expand();
        }
        if (_totalN / _bucketCount >= 5) {
            expand();
        }
        if (_bucketArray[modIndex] == null) {
            _bucketArray[modIndex] = new ArrayList<>();
        }
        _bucketArray[modIndex].add(s);
        _totalN++;
    }

    private void expand() {
        ArrayList<String>[] oldArray = _bucketArray;
        _bucketArray = new ArrayList[_bucketCount * 2];
        _bucketCount *= 2;
        _totalN = 0;
        for (ArrayList<String> arr : oldArray) {
            if (arr != null) {
                for (String str : arr) {
                    put(str);
                }
            }

        }
    }

    @Override
    public boolean contains(String s) {
        int modIndex = ((s.hashCode() % _bucketCount) + _bucketCount) % _bucketCount;
        if (_bucketArray[modIndex] != null && _bucketArray[modIndex].contains(s)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> asList() {
        ArrayList<String> returnList = new ArrayList<>();
        for (int i = 0; i < _bucketArray.length; i++) {
            if (_bucketArray[i] != null) {
                returnList.addAll(_bucketArray[i]);
            }
        }
        return returnList;
    }

    private ArrayList<String>[] _bucketArray;
    private int _totalN;
    private int _bucketCount;
    private int _defaultNumBuckets = 1;
}