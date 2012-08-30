package cellmate.extractor;

import cellmate.cell.CellReflector;
import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

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
                return getTimestamp(c, tsfieldName) != null;
            }
        });
    }

    /**
     *
     *
     * @param cells
     * @param tsfieldName
     * @param <C>
     * @return
     */
    public <C> C getCellWithMostRecentTimestamp(List<C> cells, final String tsfieldName) {
        Collection<C> cellsWithTimestamp = getCellsWithTimestamp(cells, tsfieldName);
        return Collections.max(cellsWithTimestamp, new Comparator<C>() {
            public int compare(C c, C c1) {
                Long t1 = getTimestamp(c, tsfieldName);
                Long t2 = getTimestamp(c1, tsfieldName);
                return t1.compareTo(t2);
            }
        });
    }


    public <C> Long getTimestamp(C cell, String tsFieldName) {
        Long result = getNamedAuxiliaryValue(Long.class, cell, tsFieldName);
        return result;
    }


    private <C,V> V getNamedAuxiliaryValue(Class<V> fieldType, C cell, String name){
        V result = CellReflector.getAuxiliaryValue(fieldType, cell, name);
        return result;
    }

    public <C> Collection<C> filterCellsByPredicate(List<C> cells, Predicate<? super C> predicate) {
         return Collections2.filter(cells, predicate);
    }
}
