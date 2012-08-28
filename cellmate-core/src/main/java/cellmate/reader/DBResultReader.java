package cellmate.reader;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:15 AM
 */
public interface DBResultReader<I, C> {

    public Iterable<RowIDTuples<C>> read(Iterable<I> items, int maxResultsPerQuery);
}
