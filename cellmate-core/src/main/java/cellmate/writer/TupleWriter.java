package cellmate.writer;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:04 AM
 */
public interface TupleWriter<C> {

    public void write(RowIDTuples<C> record)
        throws RuntimeException;
}
