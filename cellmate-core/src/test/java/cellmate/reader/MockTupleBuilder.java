package cellmate.reader;

import cellmate.tuple.StringValueTuple;
import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.list.PredicatedList;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:50 PM
 */
public class MockTupleBuilder implements TupleBuilder<MockDBResult, StringValueTuple>{
    public TupleBag<StringValueTuple> buildTupleBag(MockDBResult record) throws IllegalArgumentException {
        return new TupleBag<StringValueTuple>(record.getId());
    }

    public boolean hasBagLabel(MockDBResult record, String label) throws IllegalArgumentException {
        return record.getId().equals(label);
    }

    public StringValueTuple getTuple(MockDBResult record) {
        return new StringValueTuple(record.getQual(), record.getVal());
    }


    public StringValueTuple getTuple(MockDBResult record, ImmutableList<ColFamTransformer<MockDBResult>> transformers) {
        for(ColFamTransformer<MockDBResult> transformer : transformers){
            if(transformer.isMatch(record)){
                return new StringValueTuple(transformer.getLabelForColFam(record), record.getVal());
            }
        }
        return getTuple(record);
    }
}
