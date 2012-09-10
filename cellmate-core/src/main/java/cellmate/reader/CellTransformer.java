package cellmate.reader;

import cellmate.cell.CellGroup;
import com.google.common.annotations.Beta;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 6:50 PM
 */
@Beta
public interface CellTransformer<D,C> {

    public CellGroup<C> apply(D dbItem, CellGroup<C> cellGroup);
}
