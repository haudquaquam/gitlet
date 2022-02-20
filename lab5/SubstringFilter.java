/**
 * TableFilter to filter for containing substrings.
 *
 * @author Matthew Owen
 */
public class SubstringFilter extends TableFilter {

    private String _subStr;
    private String _colName;
    private Table _input;
    private int _colIndex;

    public SubstringFilter(Table input, String colName, String subStr) {
        super(input);
        _subStr = subStr;
        _colName = colName;
        _input = input;
        _colIndex = _input.colNameToIndex(_colName);
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(_colIndex).contains(_subStr)) {
            return true;
        }
        return false;
    }

    // FIXME: Add instance variables?
}
