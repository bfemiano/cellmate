package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

@Beta
public interface DBRecordWriter<D,C>  {

    public ImmutableList<D> write(Iterable<CellGroup<C>> tuples,
                                  WriteParameters parameters,
                                  DBItemTransformer<D,C> transformer) throws CellExtractorException;

    public ImmutableList<D> write(Iterable<CellGroup<C>> tuples,
                                  WriteParameters parameters) throws CellExtractorException;
}
