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
            throws NoSuchElementException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Integer> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsInt(match));
        }
        return matching;
    }

    public <C> List<String> getAllStringCellValuesWithLabel(List<C> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<String> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsString(match));
        }
        return matching;
    }

    public <C> List<Long> getAllLongCellValueByLabel(List<C> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        Collection<C> values = filterCellsByLabel(internalList, label);
        List<Long> matching = Lists.newArrayList();
        if(values.isEmpty())
            throw new NoSuchElementException("No matching values found for label: " + label);
        for(C match : values) {
            matching.add(CellReflector.getValueAsLong(match));
        }
        return matching;
    }


    public <C> String getStringCellValueByLabel(List<C> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        Collection<C> values = filterCellsByLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(values.isEmpty())
            throw new NoSuchElementException("No matching value for " + field);
        return CellReflector.getValueAsString(values.iterator().next());
    }

    public <C> double getDoubleCellValueByLabel(List<C> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("cell value for field " +
                    field + " could not be cast to double (" + value + ")");
        }
    }

    public <C> int getIntCellValueByLabel(List<C> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("cell value for field " +
                    field + " could not be cast to int (" + value + ")");
        }
    }

    public <C> long getLongCellValueByLabel(List<C> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("cell value for field " +
                    field + " could not be cast to long (" + value + ")");
        }
    }

    public <C> byte[] getBytesCellValueByLabel(List<C> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        return value.getBytes();
    }

    private <C> String internalSingleGet(List<C> tuples, String field) {
        Collection <C> values = filterCellsByLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(values.isEmpty())
            throw new NoSuchElementException("No matching value for " + field);
        return CellReflector.getValueAsString(values.iterator().next());
    }
}
