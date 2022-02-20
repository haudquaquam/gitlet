/**
 * TableFilter to filter for entries equal to a given string.
 *
 * @author Matthew Owen
 */
public class EqualityFilter extends TableFilter {

    private String _match;
    private String _colName;
    private Table _input;
    private int _colIndex;

    public EqualityFilter(Table input, String colName, String match) {
        super(input);
        _match = match;
        _colName = colName;
        _input = input;
        _colIndex = _input.colNameToIndex(_colName);
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(_colIndex).equals(_match)) {
            return true;
        }
        return false;
    }
}
