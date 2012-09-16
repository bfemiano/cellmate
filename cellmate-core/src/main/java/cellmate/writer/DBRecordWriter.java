package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * Interface DBRecordWriter
 *
 *
 * @param <D> db write object
 * @param <C> cell class type
 */
@Beta
public interface DBRecordWriter<D,C>  {

    public ImmutableList<D> write(Iterable<CellGroup<C>> groups,
                                  Parameters parameters,
                                  DBItemTransformer<D,C> transformer) throws CellExtractorException;

    public ImmutableList<D> write(Parameters parameters,
                                  DBItemTransformer<D,C> transformer) throws CellExtractorException;
}
