package cellmate.writer;

import cellmate.tuple.TupleBag;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:04 AM
 */
public interface KeyValueWriter<C> {

    public void write(TupleBag<C> record)
        throws RuntimeException;
}
