package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.tuple.TupleBag;

public interface DBRecordWriter<C, D>  {

    public void write(Iterable<TupleBag<C>> items);

    public void applyWrite(D item);

    public D getDBItem(TupleBag<C> bag);

}
