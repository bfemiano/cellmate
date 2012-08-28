package cellmate.writer;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:03 AM
 */
public class CFCommonLabelWriter<I,C> implements DBRecordWriter<C>{

    private TupleWriter<C> tupleWriter;

    public CFCommonLabelWriter(TupleWriter<C> tupleWriter) {
        this.tupleWriter = tupleWriter;
    }

    public void write(Iterable<RowIDTuples<C>> items) throws RuntimeException {
        for(RowIDTuples<C> item : items) {
             tupleWriter.write(item);
        }
    }
}
