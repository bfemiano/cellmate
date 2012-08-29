package cellmate.reader;

import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:16 AM
 */
public class TransformingDBResultReader<I,C> extends TupleBagDBResultReader<I, C> {

    private ImmutableList<ColFamTransformer<I>> transformers;

    public TransformingDBResultReader(TupleBuilder<I, C> builder,
                                      ImmutableList<ColFamTransformer<I>> transformers){
        this.tupleBuilder = builder;
        this.transformers = transformers;
    }


    @Override
    protected void applyTupleToBag(I record, TupleBag<C> cTupleBag) {
        cTupleBag.addCellTuple(tupleBuilder.getTuple(record, transformers));
    }
}
