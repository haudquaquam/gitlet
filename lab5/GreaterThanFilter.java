/**
 * TableFilter to filter for entries greater than a given string.
 *
 * @author Matthew Owen
 */
public class GreaterThanFilter extends TableFilter {

    private String _ref;
    private String _colName;
    private Table _input;
    private int _colIndex;

    public GreaterThanFilter(Table input, String colName, String ref) {
        super(input);
        _colName = colName;
        _input = input;
        _colIndex = _input.colNameToIndex(_colName);
        _ref = ref;
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(_colIndex).compareTo(_ref) > 0) {
            return true;
        }
        return false;
    }


}
