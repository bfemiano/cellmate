package cellmate.writer;

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:18 PM
 */
@Beta
public abstract class TransformingTupleToDBWriter<D,C> implements DBRecordWriter<D,C> {

    private ImmutableList<DBItemTransformer<D,C>> transformers;

    public TransformingTupleToDBWriter(ImmutableList<DBItemTransformer<D,C>> transformers) {
        this.transformers = transformers;
    }


    public D getFromTuple(Tuple<C> tuple, D dbItem) {
        for(DBItemTransformer<D,C> transformer : transformers){
            dbItem = transformer.apply(dbItem, tuple);
        }
        return dbItem;
    }
}