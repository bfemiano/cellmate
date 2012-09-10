package cellmate.writer;

import cellmate.cell.CellGroup;

/**
 * User: bfemiano
 * Date: 8/30/12
 * Time: 2:39 AM
 */
public interface DBItemTransformer<D,C> {

    public D apply(CellGroup<C> cellGroup);
}
