package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;

/**
 * Iterface CellTransformer
 *
 * produces cells and cell groups from DB read objects.
 *
 * @param <D> database item
 * @param <C> cell class type
 */
@Beta
public interface CellTransformer<D,C> {

    public CellGroup<C> apply(D dbItem, CellGroup<C> cellGroup) throws CellExtractorException;
}
