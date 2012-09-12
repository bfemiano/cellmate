package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import com.google.common.annotations.Beta;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:15 AM
 */
@Beta
public interface DBResultReader<D, C> {

    public List<CellGroup<C>> read(Iterable<D> dbItems,
                                            Parameters parameters,
                                            CellTransformer<D, C> transformer);

    public List<CellGroup<C>> read(Parameters parameters,
                                            CellTransformer<D, C> transformer);

}
