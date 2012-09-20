package cellmate.writer;


import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * Template for how to read cell groups and create database write objects.
 *
 * @param <D> database write object
 */
@Beta
public interface DBRecordWriter<D>  {

    public <C> ImmutableList<D> write(Iterable<CellGroup<C>> groups,
                                  Parameters parameters,
                                  DBItemTransformer<D,C> transformer) throws CellExtractorException;

    public <C> ImmutableList<D> write(Parameters parameters,
                                  DBItemTransformer<D,C> transformer) throws CellExtractorException;
}
