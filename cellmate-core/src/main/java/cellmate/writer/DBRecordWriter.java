package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

public interface DBRecordWriter<RECORD,
                            CTYPE extends Cell>  {

    public void write(Iterable<RowIDTuples<CTYPE>> items)
            throws RuntimeException;

}
