package cellmate.extractor;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 1:32 PM
 */
@Beta
public class CommonAuxiliaryFieldsCellExtractor implements CellExtractor{

    private SingleMultiValueCellExtractor basicExtractor =
            new SingleMultiValueCellExtractor();

    public <C> Collection<C> getCellsWithTimestamp(List<C> cells, final String tsfieldName) {
        return filterCellsByPredicate(cells, new Predicate<C>() {
            public boolean apply(C c) {
                try {
                    return getTimestamp(c, tsfieldName) != null;
                } catch (CellExtractorException e) {
                    throw new RuntimeException("Cell extraction error during predicate apply()", e);
                }
            }
        });
    }


    public <C> C getCellWithMostRecentTimestamp(List<C> cells, final String tsfieldName)
            throws CellExtractorException{
        Collection<C> cellsWithTimestamp = getCellsWithTimestamp(cells, tsfieldName);
        return Collections.max(cellsWithTimestamp, new Comparator<C>() {
            public int compare(C c, C c1) {
                try {
                    Long t1 = getTimestamp(c, tsfieldName);
                    Long t2 = getTimestamp(c1, tsfieldName);
                    return t1.compareTo(t2);
                } catch (CellExtractorException e){
                    throw new RuntimeException("Cell extraction error during comparator operation", e);
                }
            }
        });
    }


    public <C> Long getTimestamp(C cell, String tsFieldName)
            throws CellExtractorException{
        Long result = getNamedAuxiliaryValue(Long.class, cell, tsFieldName);
        return result;
    }

    public <C> Collection<C> filterCellsByPredicate(List<C> cells, Predicate<? super C> predicate) {
        return Collections2.filter(cells, predicate);
    }

    private <C,V> V getNamedAuxiliaryValue(Class<V> fieldType, C cell, String name)
            throws CellExtractorException {
        V result = CellReflector.getAuxiliaryValue(fieldType, cell, name);
        return result;
    }
}
