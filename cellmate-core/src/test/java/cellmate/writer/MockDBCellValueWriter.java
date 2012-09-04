package cellmate.writer;

import cellmate.cell.CellReflector;
import cellmate.cell.Tuple;
import cellmate.reader.MockDBResult;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 6:15 PM
 */
public class MockDBCellValueWriter<C> implements DBRecordWriter<MockMutation, C>{
    public ImmutableList<MockMutation> write(Iterable<Tuple<C>> tuples,
                                             WriteParameters parameters,
                                             DBItemTransformer<MockMutation, C> mockDBResultCDBItemTransformer) {
        throw new UnsupportedOperationException("transforming not supported");
    }

    public ImmutableList<MockMutation> write(Iterable<Tuple<C>> tuples, WriteParameters parameters) {
        ImmutableList.Builder<MockMutation> list = ImmutableList.builder();
        for(Tuple<C> tuple : tuples){
            MockMutation result = new MockMutation(tuple.getTag());
            for(C cell : tuple.getInternalList()) {
                String qual = CellReflector.getLabelAsString(cell);
                String val = CellReflector.getValueAsString(cell);
                String colFam= null;
                try {
                    colFam = CellReflector.getColFam(cell);
                } catch (NoSuchFieldException e) {
                    colFam = "cf";
                }
                result.addItem(new MockMutation.MockColQualVal(colFam, qual, val.getBytes()));
            }
            list.add(result);
        }
        return list.build();
    }
}
