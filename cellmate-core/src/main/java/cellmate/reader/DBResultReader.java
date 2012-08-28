package cellmate.reader;

import cellmate.tuple.Cell;
import cellmate.tuple.RowIDTuples;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:15 AM
 */
public interface DBResultReader<RESULT,
                                CTYPE extends Cell> {

    public Iterable<RowIDTuples<CTYPE>> read(Iterable<RESULT> items, int maxResultsPerQuery);
}
