package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 6:15 PM
 */
public class MockDBCellValueWriter implements DBRecordWriter<MockMutation>{

    private DBRecordWriter<MockMutation> dbWriter = new BasicCelltoRecordWriter<MockMutation>();

    public <C> ImmutableList<MockMutation> write(Parameters parameters,
                                             DBItemTransformer<MockMutation, C> mockDBResultCDBItemTransformer)
            throws CellExtractorException {
        return dbWriter.write(parameters, mockDBResultCDBItemTransformer);
    }

    public <C> ImmutableList<MockMutation> write(Iterable<CellGroup<C>> groups,
                                             Parameters parameters,
                                             DBItemTransformer<MockMutation, C> transformer)
        throws CellExtractorException {
        return dbWriter.write(groups, parameters, transformer);

    }
}
