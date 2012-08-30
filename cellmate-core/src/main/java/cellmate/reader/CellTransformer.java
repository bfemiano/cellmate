package cellmate.reader;

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 6:50 PM
 */
@Beta
public interface CellTransformer<D,C> {

    public Tuple<C> apply(D dbItem, Tuple<C> tuple);
}
