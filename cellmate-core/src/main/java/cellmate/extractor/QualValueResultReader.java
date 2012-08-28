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
public class QualValueResultReader<RESULT, CTYPE extends Cell>
        implements DBResultReader<RESULT, CTYPE> {
     private TupleResultParser<RESULT, CTYPE> cellTupleBuilder;

    public QualValueResultReader(TupleResultParser<RESULT, CTYPE> parser) {
        this.cellTupleBuilder = parser;
    }

    public List<RowIDTuples<CTYPE>> read(Iterable<RESULT> iter, int maxResultsPerQuery) {
        List<RowIDTuples<CTYPE>> results = new ArrayList<RowIDTuples<CTYPE>>();
        RowIDTuples<CTYPE> result = null;
        Iterator<RESULT> itar = iter.iterator();
        for(int i = 0; i < maxResultsPerQuery & itar.hasNext(); i++){
            RESULT record = itar.next();
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
