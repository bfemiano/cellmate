package cellmate.writer;

import cellmate.cell.CellGroup;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:18 PM
 */
@Beta
public class DBTransformerTupleRecordWriter<D,C> implements DBRecordWriter<D,C> {


    public ImmutableList<D> write(Iterable<CellGroup<C>> tuples, WriteParameters parameters, DBItemTransformer<D,C> transformer) {
        ImmutableList.Builder<D> list = ImmutableList.builder();
        D dbItem = null;
        for(CellGroup<C> cellGroup : tuples){
            dbItem = transformer.apply(cellGroup);
            list.add(dbItem);
        }
        return list.build();
    }

    public ImmutableList<D> write(Iterable<CellGroup<C>> tuples, WriteParameters parameters) {
        throw new UnsupportedOperationException("this implementation only supports transform operations");
    }
}