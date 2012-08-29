package cellmate.writer;

import cellmate.reader.ColFamTransformer;
import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:18 PM
 */
public abstract class TransformingTupleBagDBWriter<C, V> extends TupleBagDBWriter<C, V> {

    protected ImmutableList<ColFamTransformer<C>> transformers;

    public abstract void applyWrite(V item);

    @Override
    public V getDBItem(TupleBag<C> dbItem) {
        return keyValueProducer.produce(dbItem, transformers);
    }
}