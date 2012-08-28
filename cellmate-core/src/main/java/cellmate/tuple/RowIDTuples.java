package cellmate.tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Designed to hold the latest qualifer/values pairs for a given row.
 *
 * Delegates to an internal list holding the tuple tuples.
 *
 * User: bfemiano
 * Date: 8/25/12
 * Time: 1:31 PM
 */
public class RowIDTuples<CTYPE extends Cell> implements Iterable<CTYPE>{

    List<CTYPE> pairs = new ArrayList<CTYPE>();

    private String rowId;

    public RowIDTuples(String rowId) {
        this.rowId = rowId;
    }

    public void addCellTuple(CTYPE pair) {
        pairs.add(pair);
    }

    public List<CTYPE> getInternalList() {
       return pairs;
    }

    public Iterator<CTYPE> iterator() {
        return pairs.iterator();
    }

    public String getRowId() {
        return rowId;
    }
}
