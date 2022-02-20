/**
 * TableFilter to filter for entries whose two columns match.
 *
 * @author Matthew Owen
 */
public class ColumnMatchFilter extends TableFilter {

    private String _colName1;
    private String _colName2;
    private Table _input;
    private int _colIndex1;
    private int _colIndex2;

    public ColumnMatchFilter(Table input, String colName1, String colName2) {
        super(input);
        _colName1 = colName1;
        _colName2 = colName2;
        _input = input;
        _colIndex1 = _input.colNameToIndex(_colName1);
        _colIndex2 = _input.colNameToIndex(_colName2);
    }

    @Override
    protected boolean keep() {
        if (candidateNext().getValue(_colIndex1).equals(candidateNext().getValue(_colIndex2))) {
            return true;
        }
        return false;
    }

}
