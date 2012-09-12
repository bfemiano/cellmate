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
                    throw new RuntimeException("Error during filtering " + e.getType().name(),e);
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
                    throw new RuntimeException("Error during filtering " + e.getType().name(),e);
                }
            }
        });
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

    public <C> long getLongValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsLong(value);
    }

    public <C> int getIntValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsInt(value);
    }

    public <C> double getDoubleValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsDouble(value);
    }

    public <C> byte[] getBytesValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value= getSingleCellByLabel(cells, label);
        byte[] result = CellReflector.getValueAsBytes(value);
        return result;
    }

    public <C> String getStringValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        String result =  CellReflector.getValueAsString(value);
        return result;
    }

    public <T,C> T getTypedValueByLabel(Class<T> type, List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        T result = CellReflector.getValueAsInstance(type, value);
        return result;
    }

    public <C> C getSingleCellByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection <C> values = filterCellsByLabel(cells, label);
        checkForOneCell(values);
        return values.iterator().next();
    }

    public <C> int getIntValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsInt(cells.iterator().next());
    }

    public <C> double getDoubleValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsDouble(cells.iterator().next());
    }

    public <C> long getLongValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsLong(cells.iterator().next());
    }

    public <C> String getStringValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsString(cells.iterator().next());
    }

    public <C> byte[] getBytesValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsBytes(cells.iterator().next());
    }

    public <T,C> T getTypedValueFromFirstItem(Class<T> cls, List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsInstance(cls, cells.iterator().next());
    }

    private <C> void checkForOneCell(Collection<C> cells) throws CellExtractorException {
        if(cells.size() > 1)
            throw new CellExtractorException("Too many values for single cell get", ErrorType.TOO_MANY_FIELDS);
        if(cells.size() == 0)
            throw new CellExtractorException("No value for single get", ErrorType.MISSING_FIELD);
    }
}
