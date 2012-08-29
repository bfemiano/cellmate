package cellmate.reader;

import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:15 AM
 */
public interface DBResultReader<I, C> {

    public ImmutableList<TupleBag<C>> read(Iterable<I> items, int maxResultsPerQuery);

    public ImmutableList<TupleBag<C>> read(Iterable<I> items);
}
