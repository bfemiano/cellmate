package cellmate.reader;


import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 2:28 PM
 */
public interface TupleResultParser<I, C> {

    public RowIDTuples<C> buildRowIDTuple(I record)
            throws IllegalArgumentException;

    public boolean rowIDsEqual(I record, String id)
            throws IllegalArgumentException;

    public C getTuple(I record);

    public C getTuple(I record, ColFamGroup[] group);

    public C getTuple(String label, String value);
}
