package cellmate.writer;

import cellmate.tuple.TupleBag;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 11:56 AM
 */
public abstract class SimpleTupleBagDBWriter<C, V> extends TupleBagDBWriter<C, V> {


    public abstract void applyWrite(V item);

    @Override
    public V getDBItem(TupleBag<C> dbItem) {
        return keyValueProducer.produce(dbItem);
    }
}
