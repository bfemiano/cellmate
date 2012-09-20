package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;

import java.util.List;

/**
 * Allows customizable database write object generation based on
 * the contents of a particular cell group.
 *
 *
 * @param <D> datbase write object
 * @param <C>  cell class type
 */
public interface DBItemTransformer<D,C> {

    public List<D> apply(CellGroup<C> cellGroup) throws CellExtractorException;
}
