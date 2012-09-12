package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/30/12
 * Time: 2:39 AM
 */
public interface DBItemTransformer<D,C> {

    public List<D> apply(CellGroup<C> cellGroup) throws CellExtractorException;
}
