package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import com.google.common.annotations.Beta;

import java.util.List;

/**
 * Template for how to read database scan results and apply a cell transformer.
 *
 * @param <D> database item
 */
@Beta
public interface DBResultReader<D> {

    public <C> List<CellGroup<C>> read(Iterable<D> dbItems,
                                            Parameters parameters,
                                            CellTransformer<D, C> transformer);

    public <C> List<CellGroup<C>> read(Parameters parameters,
                                            CellTransformer<D, C> transformer);

}
