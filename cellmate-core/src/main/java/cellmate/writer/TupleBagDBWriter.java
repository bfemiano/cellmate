package cellmate.writer;

import cellmate.tuple.TupleBag;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:03 AM
 */
public abstract class TupleBagDBWriter<C, V> implements DBRecordWriter<C, V> {

    protected KeyValueProducer<C, V> keyValueProducer;

    public void write(Iterable<TupleBag<C>> bagList)  {
        for(TupleBag<C> bag : bagList) {
             V item = getDBItem(bag);
            applyWrite(item);
        }
    }

    public abstract void applyWrite(V item);

    public abstract V getDBItem(TupleBag<C> bag);
}
