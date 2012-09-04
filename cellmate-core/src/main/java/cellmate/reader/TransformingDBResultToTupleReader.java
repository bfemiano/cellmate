package cellmate.reader;

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:16 AM
 */
@Beta
public class TransformingDBResultToTupleReader<D,C> extends DBResultToTupleReader<D, C> {

    private Collection<CellTransformer<D,C>> transformers;

    public TransformingDBResultToTupleReader(Collection<CellTransformer<D,C>> transformers){
        this.transformers = transformers;
    }


    public Tuple<C> addCells(D record, Tuple<C> tuple) {
        for(CellTransformer<D,C> transformer : transformers){
            tuple = transformer.apply(record, tuple);
        }
        return tuple;
    }
}
