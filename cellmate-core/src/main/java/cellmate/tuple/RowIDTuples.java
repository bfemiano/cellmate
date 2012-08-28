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
@Container
public class RowIDTuples<C> implements Iterable<C>{

    List<C> pairs = new ArrayList<C>();

    private String rowId;

    public RowIDTuples(String rowId) {
        this.rowId = rowId;
    }

    public void addCellTuple(C pair) {
        pairs.add(pair);
    }

    public List<C> getInternalList() {
       return pairs;
    }

    public Iterator<C> iterator() {
        return pairs.iterator();
    }

    @Group
    public String getRowId() {
        return rowId;
    }
}
