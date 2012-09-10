package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 6:15 PM
 */
public class MockDBCellValueWriter<C> implements DBRecordWriter<MockMutation, C>{
    public ImmutableList<MockMutation> write(Iterable<CellGroup<C>> tuples,
                                             WriteParameters parameters,
                                             DBItemTransformer<MockMutation, C> mockDBResultCDBItemTransformer) {
        throw new UnsupportedOperationException("transforming not supported");
    }

    public ImmutableList<MockMutation> write(Iterable<CellGroup<C>> tuples, WriteParameters parameters)
        throws CellExtractorException {
        ImmutableList.Builder<MockMutation> list = ImmutableList.builder();
        for(CellGroup<C> cellGroup : tuples){
            MockMutation result = new MockMutation(cellGroup.getTag());
            for(C cell : cellGroup.getInternalList()) {
                String qual = CellReflector.getLabelAsString(cell);
                byte[] valueBytes = CellReflector.getValueBytesIfPrimative(cell);
                String colFam;
                colFam = CellReflector.getColFam(cell);
                result.addItem(new MockMutation.MockColQualVal(colFam, qual, valueBytes));
            }
            list.add(result);
        }
        return list.build();
    }
}
