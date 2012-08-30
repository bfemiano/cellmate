package cellmate.writer;

import cellmate.cell.Tuple;

/**
 * User: bfemiano
 * Date: 8/30/12
 * Time: 2:39 AM
 */
public interface DBItemTransformer<D,C> {

    public D apply(D dbRecord, Tuple<C> tuple);
}
