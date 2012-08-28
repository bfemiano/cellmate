package cellmate.reader;


import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 2:28 PM
 */
public interface TupleResultParser<RESULT, CTYPE extends Cell> {

    public RowIDTuples<CTYPE> buildRowIDTuple(RESULT record)
            throws IllegalArgumentException;

    public boolean rowIDsEqual(RESULT record, String id)
            throws IllegalArgumentException;

    public CTYPE getTuple(RESULT record);

    public CTYPE getTuple(RESULT record, ColFamGroup[] group);

    public CTYPE getTuple(String label, String value);
}
