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
public class MockDBCellValueWriter<C> implements DBRecordWriter<MockMutation, C>{

    private DBRecordWriter<MockMutation, C> dbWriter = new BasicCelltoRecordWriter<MockMutation, C>();

    public ImmutableList<MockMutation> write(Parameters parameters,
                                             DBItemTransformer<MockMutation, C> mockDBResultCDBItemTransformer)
            throws CellExtractorException {
        return dbWriter.write(parameters, mockDBResultCDBItemTransformer);
    }

    public ImmutableList<MockMutation> write(Iterable<CellGroup<C>> groups,
                                             Parameters parameters,
                                             DBItemTransformer<MockMutation, C> transformer)
        throws CellExtractorException {
        return dbWriter.write(groups, parameters, transformer);

    }
}
