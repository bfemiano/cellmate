package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.tuple.TupleBag;

public interface DBRecordWriter<C>  {

    public void write(Iterable<TupleBag<C>> items)
            throws RuntimeException;

}
