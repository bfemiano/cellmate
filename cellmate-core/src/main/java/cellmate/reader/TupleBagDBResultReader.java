package cellmate.reader;


import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:14 AM
 */
public abstract class TupleBagDBResultReader<I,C> implements DBResultReader<I, C> {

    protected TupleBuilder<I, C> tupleBuilder;

    public ImmutableList<TupleBag<C>> read(Iterable<I> items, int maxResultsPerQuery) {
        ImmutableList.Builder<TupleBag<C>> list = ImmutableList.builder();
        TupleBag<C> result = null;
        Iterator<I> itar = items.iterator();
        for(int i = 0; i < maxResultsPerQuery & itar.hasNext(); i++){
            I record = itar.next();
            if(i == 0)
                result = tupleBuilder.buildTupleBag(record);
            if(!tupleBuilder.hasBagLabel(record, result.getBagLabel())) {
                list.add(result);
                result = tupleBuilder.buildTupleBag(record);
            }

            applyTupleToBag(record, result);
            if(!itar.hasNext() | (i + 1) == maxResultsPerQuery) //add the last one
                list.add(result);
        }
        return list.build();
    }

    protected abstract void applyTupleToBag(I record, TupleBag<C> result);

    public ImmutableList<TupleBag<C>> read(Iterable<I> items) {
        return read(items, Integer.MAX_VALUE);
    }


}
