package cellmate.writer;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:04 AM
 */
public interface TupleWriter<CTYPE extends Cell> {

    public void write(RowIDTuples<CTYPE> record)
        throws RuntimeException;
}
