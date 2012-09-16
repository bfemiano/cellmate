package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;

import java.util.List;

/**
 * Iterface DBItemTransformer
 *
 * Transformers cells to db write objects.
 *
 *
 * @param <D> datbase write object
 * @param <C>  cell class type
 */
public interface DBItemTransformer<D,C> {

    public List<D> apply(CellGroup<C> cellGroup) throws CellExtractorException;
}
