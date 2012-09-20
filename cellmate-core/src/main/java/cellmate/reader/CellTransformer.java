package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;

/**
 * Produces cells and cell groups from DB read objects.
 *
 * Concrete class can define any business rules for domain object generation
 * based on the key/value pairs that return from the datbase.
 *
 * @param <D> database item
 * @param <C> cell type
 */
@Beta
public interface CellTransformer<D,C> {

    /**
     *
     *
     * @param dbItem  incoming key/value pair
     * @param cellGroup group object. Stays the same reference until changed by the transformer.

     * @return CellGroup containing cell results after key/value inspection.
     * @throws CellExtractorException if error occurs reading cell contents.
     */
    public CellGroup<C> apply(D dbItem, CellGroup<C> cellGroup) throws CellExtractorException;
}
