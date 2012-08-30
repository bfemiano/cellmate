package cellmate.reader;

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:15 AM
 */
@Beta
public interface DBResultReader<D, C> {

    public ImmutableList<Tuple<C>> read(Iterable<D> dbItems, int maxResultsPerQuery);

    public ImmutableList<Tuple<C>> read(Iterable<D> dbItems);

    public Tuple<C> addCells(D record, Tuple<C> tuple);
}
