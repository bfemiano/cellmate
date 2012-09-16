package cellmate.extractor;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Basic extractor implementation that offers cell filter capability by
 * provided Predicate rules.
 *
 * @see {@link CellReflector}
 */
@Beta
public class SingleMultiValueCellExtractor
        implements CellExtractor {

    /**
     * Reads a list of cells and returns a filtered list containing
     * only those with the matching label.
     *
     * @param cells list of cells to filter
     * @param label to filter cells on.
     * @param <C> cell class type
     * @return filtered collection.
     * @throws RuntimeException if CellExtractionException is encountered
     */
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

    /**
     *
     * Read a list of cells and return only those that return true for the given Predicate.
     *
     * @param cells list of cells to filter.
     * @param predicate rule for filtering.
     * @param <C> cell type class.
     * @return filtered collection.
     */
    public <C> Collection<C> filterCellsByPredicate(List<C> cells, Predicate<? super C> predicate) {
        return Collections2.filter(cells,predicate);
    }

    /**
     * Reads a list of cells and returns a filtered list containing
     * only those that match the supplied regex.
     *
     * @param cells list of cells to filter
     * @param regex to filter cells on.
     * @param <C> cell class type
     * @return filtered collection.
     * @throws RuntimeException if CellExtractionException is encountered
     */
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

    /**
     * Return a list of int values for any cells in the group matching
     * the supplied label
     *
     * @param cells to filter.
     * @param label to filter on.
     * @param <C>  cell class type
     * @return  value collections as int.
     * @throws CellExtractorException if errors occur during cell value reading.
     */
    public <C> Collection<Integer> getAllIntCellValuesWithLabel(List<C> cells, String label)
           throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(cells, label);
        List<Integer> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsInt(match));
        }
        return matching;
    }

    /**
     * Return a list of String values for any cells in the group matching
     * the supplied label
     *
     * @param cells to filter.
     * @param label to filter on.
     * @param <C>  cell class type
     * @return  value collections as String.
     * @throws CellExtractorException if errors occur during cell value reading.
     */
    public <C> List<String> getAllStringCellValuesWithLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(cells, label);
        List<String> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsString(match));
        }
        return matching;
    }

    /**
     * Return a list of long values for any cells in the group matching
     * the supplied label
     *
     * @param cells to filter.
     * @param label to filter on.
     * @param <C>  cell class type
     * @return  value collections as long.
     * @throws CellExtractorException if errors occurs while reading cell value, including multiple values found.
     */
    public <C> List<Long> getAllLongCellValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection<C> values = filterCellsByLabel(cells, label);
        List<Long> matching = Lists.newArrayList();
        for(C match : values) {
            matching.add(CellReflector.getValueAsLong(match));
        }
        return matching;
    }

    /**
     * Expects to only find one cell with the given label and return the value
     * for that cell as a long.
     *
     * @param cells to search for label.
     * @param label label to search on.
     * @param <C> cell class type
     * @return long value.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> long getLongValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsLong(value);
    }

    /**
     * Expects to only find one cell with the given label and return the value
     * for that cell as a int.
     *
     * @param cells to search for label.
     * @param label label to search on.
     * @param <C> cell class type
     * @return int value.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> int getIntValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsInt(value);
    }

    /**
     * Expects to only find one cell with the given label and return the value
     * for that cell as a double.
     *
     * @param cells to search for label.
     * @param label label to search on.
     * @param <C> cell class type
     * @return double value.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> double getDoubleValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        return CellReflector.getValueAsDouble(value);
    }

    /**
     * Expects to only find one cell with the given label and return the value
     * for that cell as a byte[].
     *
     * @param cells to search for label.
     * @param label label to search on.
     * @param <C> cell class type
     * @return byte[] value.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> byte[] getBytesValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value= getSingleCellByLabel(cells, label);
        byte[] result = CellReflector.getValueAsBytes(value);
        return result;
    }
    /**
     * Expects to only find one cell with the given label and return the value
     * for that cell as a String.
     *
     * @param cells to search for label.
     * @param label label to search on.
     * @param <C> cell class type
     * @return String value.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> String getStringValueByLabel(List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        String result =  CellReflector.getValueAsString(value);
        return result;
    }

    /**
     *  Expects to only find one cell with the given label and return the value
     *  for that cell as the supplied Class value.
     *
     * @param type class of value type.
     * @param cells to filter.
     * @param label to filter on.
     * @param <T> value type class
     * @param <C>  cell type class
     * @return T value.
     * @throws CellExtractorException
     */
    public <T,C> T getTypedValueByLabel(Class<T> type, List<C> cells, String label)
            throws CellExtractorException {
        C value = getSingleCellByLabel(cells, label);
        T result = CellReflector.getValueAsInstance(type, value);
        return result;
    }

    /**
     * Expects to find one cell in the collection with the supplied label.
     *
     * @param cells to filter.
     * @param label to filter on.
     * @param <C> cell class type
     * @return Cell found, if any.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> C getSingleCellByLabel(List<C> cells, String label)
            throws CellExtractorException {
        Collection <C> values = filterCellsByLabel(cells, label);
        checkForOneCell(values);
        return values.iterator().next();
    }

    /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as an int.
     *
     *
     * @param cells list
     * @param <C> cell class type
     * @return int value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> int getIntValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsInt(cells.iterator().next());
    }

    /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as a double.
     *
     *
     * @param cells list
     * @param <C> cell class type
     * @return double value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> double getDoubleValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsDouble(cells.iterator().next());
    }

    /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as a long.
     *
     *
     * @param cells list
     * @param <C> cell class type
     * @return long value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> long getLongValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsLong(cells.iterator().next());
    }

    /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as a String.
     *
     *
     * @param cells list
     * @param <C> cell class type
     * @return String value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> String getStringValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsString(cells.iterator().next());
    }

    /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as a byte[].
     *
     *
     * @param cells list
     * @param <C> cell class type
     * @return byte[] value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <C> byte[] getBytesValueFromFirstCell(List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsBytes(cells.iterator().next());
    }

    /**
     *
     * /**
     * Assumes a list of at least one cell and returns the value from
     * the first item in the list as an instance of class T.
     * @param <T> value class type
     * @param cells list
     * @param <C> cell class type
     * @return T value of first cell found.
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found.
     */
    public <T,C> T getTypedValueFromFirstItem(Class<T> cls, List<C> cells)
            throws CellExtractorException {
        checkForOneCell(cells);
        return CellReflector.getValueAsInstance(cls, cells.iterator().next());
    }

    /**
     * Return the label from the supplied cell as a String, if any.
     *
     * @param cell to lookup label
     * @param <C> cell class type
     * @return String label
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found or null label.
     */
    public <C> String getLabel(C cell) throws CellExtractorException {
       return CellReflector.getLabelAsString(cell);
    }

    /**
     * Return the value from the supplied cell as a String, if any.
     *
     * @param cell to lookup value
     * @param <C> cell class type
     * @return String value
     * @throws CellExtractorException if error occurs while reading cell value, including multiple values found or null value.
     */
    public <C> String getStringValue(C cell) throws CellExtractorException {
        return CellReflector.getValueAsString(cell);
    }

    private <C> void checkForOneCell(Collection<C> cells) throws CellExtractorException {
        if(cells.size() > 1)
            throw new CellExtractorException("Too many values for single cell get", ErrorType.TOO_MANY_FIELDS);
        if(cells.size() == 0)
            throw new CellExtractorException("No value for single get", ErrorType.MISSING_FIELD);
    }
}
