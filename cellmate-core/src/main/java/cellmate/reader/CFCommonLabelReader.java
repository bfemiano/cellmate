package cellmate.reader;


import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

import java.util.*;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:14 AM
 */
public class CFCommonLabelReader<RESULT,
                                        CTYPE extends Cell>
        implements DBResultReader<RESULT, CTYPE> {

    private List<ColFamGroup> colFamsToApplyConstantLabel = Collections.emptyList();
    private TupleResultParser<RESULT, CTYPE> cellTupleBuilder;

    public CFCommonLabelReader(TupleResultParser<RESULT, CTYPE> parser) {
        this.cellTupleBuilder = parser;
    }

    public CFCommonLabelReader(TupleResultParser<RESULT, CTYPE> parser,
                               ColFamGroup[] groups) {
        this.cellTupleBuilder = parser;
        this.colFamsToApplyConstantLabel = Arrays.asList(groups);
    }

    public void setColFamsToApplyConstantLabel(ColFamGroup[] groups) {
        this.colFamsToApplyConstantLabel = Arrays.asList(groups);
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
            result.addCellTuple(cellTupleBuilder.getTuple(record,
                    colFamsToApplyConstantLabel.toArray(new ColFamGroup[]{})));
            if(!itar.hasNext() | (i + 1) == maxResultsPerQuery) //add the last one
                results.add(result);
        }
        return results;
    }


}
