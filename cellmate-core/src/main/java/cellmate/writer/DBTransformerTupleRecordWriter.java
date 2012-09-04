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
public class DBTransformerTupleRecordWriter<D,C> implements DBRecordWriter<D,C> {


    public ImmutableList<D> write(Iterable<Tuple<C>> tuples, WriteParameters parameters, DBItemTransformer<D,C> transformer) {
        ImmutableList.Builder<D> list = ImmutableList.builder();
        D dbItem = null;
        for(Tuple<C> tuple : tuples){
            dbItem = transformer.apply(tuple);
            list.add(dbItem);
        }
        return list.build();
    }

    public ImmutableList<D> write(Iterable<Tuple<C>> tuples, WriteParameters parameters) {
        throw new UnsupportedOperationException("this implementation only supports transform operations");
    }
}