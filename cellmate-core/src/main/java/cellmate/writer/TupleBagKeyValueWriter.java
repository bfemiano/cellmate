package cellmate.writer;

import cellmate.tuple.TupleBag;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:03 AM
 */
public abstract class TupleBagKeyValueWriter<C> implements DBRecordWriter<C>{

    protected KeyValueWriter<C> keyValueWriter;

    public TupleBagKeyValueWriter(KeyValueWriter<C> keyValueWriter) {
        this.keyValueWriter = keyValueWriter;
    }

    public void write(Iterable<TupleBag<C>> items) throws RuntimeException {
        for(TupleBag<C> item : items) {
             keyValueWriter.write(item);
        }
    }


}
