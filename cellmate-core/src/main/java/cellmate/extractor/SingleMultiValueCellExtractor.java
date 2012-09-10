package cellmate.extractor;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 *
 * User: bfemiano
 * Date: 8/25/12
 * Time: 1:41 PM
 */
@Beta
public class SingleMultiValueCellExtractor
        implements CellExtractor {

    public <C> Collection<C> filterCellsByLabel(List<C> cells, final String label) {
        return Collections2.filter(cells, new Predicate<C>() {
            public boolean apply(C c) {
                try {
                    String cellLabel = CellReflector.getLabelAsString(c);
                    return cellLabel.equals(label);
                } catch (CellExtractorException e){
                    return false;
                }
            }
        });
    }

    public <C> Collection<C> filterCellsByPredicate(List<C> cells, Predicate<? super C> predicate) {
        return Collections2.filter(cells,predicate);
    }

    public <C> Collection<C> regexMatchLabel(List<C> cells, final String regex) {
        return filterCellsByPredicate(cells, new Predicate<C>() {
            public boolean apply(C c) {
                try {
                    return  Pattern.matches(regex, CellReflector.getLabelAsString(c));
                } catch (CellExtractorException e){
                    //TODO log this exception.
                    return false;
                }
            }
        });
    }

    public <C> boolean hasMoreThanOne(Collection<C> values)
            throws IllegalArgumentException{
        return values.size() > 1;
    }

    public <C> Collection<Integer> getAllIntCellValuesWithLabel(List<C> internalList, String label)
           throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Integer> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsInt(match));
        }
        return matching;
    }

    public <C> List<String> getAllStringCellValuesWithLabel(List<C> internalList, String label)
            throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<String> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsString(match));
        }
        return matching;
    }

    public <C> List<Long> getAllLongCellValueByLabel(List<C> internalList, String label)
            throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Long> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsLong(match));
        }
        return matching;
    }

    private <T,C> T getTypedValueByLabel(Class<T> type, List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        T result = CellReflector.getValueAsInstance(type, values.iterator().next());
        return result;
    }

    public <C> long getLongValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        return CellReflector.getValueAsLong(values.iterator().next());
    }

    public <C> int getIntValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        return CellReflector.getValueAsInt(values.iterator().next());
    }

    public <C> double getDoubleValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        return CellReflector.getValueAsDouble(values.iterator().next());
    }

    public <C> byte[] getBytesValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        byte[] result = CellReflector.getValueAsBytes(values.iterator().next());
        return result;
    }

    public <C> String getStringValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        String result =  CellReflector.getValueAsString(values.iterator().next());
        return result;
    }

    private <C> Collection<C> getSingleCell(List<C> cells, String field)
            throws CellExtractorException {
        Collection <C> values = filterCellsByLabel(cells, field);
        return values;
    }
}
