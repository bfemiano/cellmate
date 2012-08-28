package cellmate.writer;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:03 AM
 */
public class CFCommonLabelWriter<RECORD,
                                 CTYPE extends Cell> implements DBRecordWriter<RECORD, CTYPE>{

    private TupleWriter<CTYPE> tupleWriter;

    public CFCommonLabelWriter(TupleWriter<CTYPE> tupleWriter) {
        this.tupleWriter = tupleWriter;
    }

    public void write(Iterable<RowIDTuples<CTYPE>> items)
            throws RuntimeException {
        for(RowIDTuples<CTYPE> item : items) {
             tupleWriter.write(item);
        }
    }
}
