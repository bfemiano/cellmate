package cellmate.extractor;

import cellmate.reader.TupleResultParser;
import cellmate.tuple.Cell;
import cellmate.reader.DBResultReader;
import cellmate.tuple.RowIDTuples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 11:08 PM
 */
public class QualValueResultReader<I, C>
        implements DBResultReader<I, C> {
     private TupleResultParser<I, C> cellTupleBuilder;

    public QualValueResultReader(TupleResultParser<I, C> parser) {
        this.cellTupleBuilder = parser;
    }

    public List<RowIDTuples<C>> read(Iterable<I> iter, int maxResultsPerQuery) {
        List<RowIDTuples<C>> results = new ArrayList<RowIDTuples<C>>();
        RowIDTuples<C> result = null;
        Iterator<I> itar = iter.iterator();
        for(int i = 0; i < maxResultsPerQuery & itar.hasNext(); i++){
            I record = itar.next();
            if(i == 0)
                result = cellTupleBuilder.buildRowIDTuple(record);
            if(!cellTupleBuilder.rowIDsEqual(record, result.getRowId())) {
                results.add(result);
                result = cellTupleBuilder.buildRowIDTuple(record);
            }
            result.addCellTuple(cellTupleBuilder.getTuple(record));
            if(!itar.hasNext() | (i + 1) == maxResultsPerQuery) //add the last one
                results.add(result);
        }
        return results;
    }
}
