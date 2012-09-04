package cellmate.extractor;

import cellmate.cell.CellReflector;
import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
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
                String cellLabel = CellReflector.getLabelAsString(c);
                return cellLabel != null ? cellLabel.equals(label) : false;
            }
        });
    }

    public <C> Collection<C> filterCellsByPredicate(List<C> cells, Predicate<? super C> predicate) {
        return Collections2.filter(cells,predicate);
    }

    public <C> Collection<C> regexMatchLabel(List<C> cells, final String regex) {
        return filterCellsByPredicate(cells, new Predicate<C>() {
            public boolean apply(C c) {
                String cellLabel = CellReflector.getLabelAsString(c);
                return cellLabel != null ? Pattern.matches(
                        regex, CellReflector.getLabelAsString(c)) : false;
            }
        });
    }

    public <C> boolean hasMoreThanOne(Collection<C> values)
            throws IllegalArgumentException{
        return values.size() > 1;
    }

    public <C> Collection<Integer> getAllIntCellValuesWithLabel(List<C> internalList, String label)
            throws CellExtractorException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Integer> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new CellExtractorException("No matching values found for regex: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsInt(match));
        }
        return matching;
    }

    public <C> List<String> getAllStringCellValuesWithLabel(List<C> internalList, String label)
            throws CellExtractorException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<String> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new CellExtractorException("No matching values found for regex: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsString(match));
        }
        return matching;
    }

    public <C> List<Long> getAllLongCellValueByLabel(List<C> internalList, String label)
            throws CellExtractorException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Long> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new CellExtractorException("No matching values found for label: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsLong(match));
        }
        return matching;
    }

    private <T,C> T getTypedValueByLabel(Class<T> type, List<C> cells, String label)
            throws CellExtractorException {
         Collection<C> values = getSingleCell(cells, label);
         T result = CellReflector.getValueAsInstance(type, values.iterator().next());
         if(result == null){
             throw new CellExtractorException("found null when looking up " + type.getName() +
             " for cell with label " + label);
         }
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
        if(result == null){
            throw new CellExtractorException("found null when looking up byte[] value for cell with label: " + label);
        }
        return result;
    }

    public <C> String getStringValueByLabel(List<C> cells, String label)
        throws CellExtractorException {
        Collection<C> values = getSingleCell(cells, label);
        String result =  CellReflector.getValueAsString(values.iterator().next());
        if(result == null)
            throw new CellExtractorException("found null when looking up string value for cell with label: " + label);
        return result;
    }

    private <C> Collection<C> getSingleCell(List<C> cells, String field)
        throws CellExtractorException {
        Collection <C> values = filterCellsByLabel(cells, field);
        if(hasMoreThanOne(values))
            throw new CellExtractorException("Too many matching values for cell with label: " + field);
        if(values.isEmpty())
            throw new CellExtractorException("No matching value for cell with label:  " + field);
        return values;
    }
}
