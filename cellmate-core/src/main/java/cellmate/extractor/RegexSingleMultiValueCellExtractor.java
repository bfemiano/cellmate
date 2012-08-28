package cellmate.extractor;

import cellmate.tuple.Cell;
import cellmate.tuple.CellTuple;

import java.util.ArrayList;
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
public class RegexSingleMultiValueCellExtractor<T extends Cell>
        implements CellExtractor<T> {

    public List<T> matchLabel(List<T> tuples, String label) {
        List<T> values = new ArrayList<T>();
        for(T tuple : tuples) {
            if(tuple.getLabel().equals(label))
                values.add(tuple);
        }
        return values;
    }

    public T getMostRecentTimestamp(List<T> tuples) {
        T maxT = null;
        long max = Long.MIN_VALUE;
        for(T t : tuples){
            if(t.getTimestamp() > max) {
                maxT = t;
                max = t.getTimestamp();
            }
        }
        return maxT;
    }


    public List<T> regexMatchLabel(List<T> tuples, String regex) {
        List<T> values = new ArrayList<T>();
        for(T tuple : tuples) {
            if(Pattern.matches(regex, tuple.getLabel()))
                values.add(tuple);
        }
        return values;
    }

    public boolean hasMoreThanOne(List<T> values)
            throws IllegalArgumentException{
        return values.size() > 1;
    }

    public boolean isEmpty(List<T> values)
            throws IllegalArgumentException{
        return values.size() == 0;
    }

    public List<Integer> getIntList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<Integer> matching = new ArrayList<Integer>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(Cell match : values) {
            try {
                matching.add(Integer.parseInt(match.getValue()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("tuple value for field " +
                        label + " could not be cast to int (" + match + ")");
            }
        }
        return matching;
    }

    public List<String> getStringList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<String> matching = new ArrayList<String>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(Cell match : values) {
            try {
                matching.add(match.getValue());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("tuple value for field " +
                        label + " could not be cast to int (" + match + ")");
            }
        }
        return matching;
    }

    public List<Long> getLongList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<Long> matching = new ArrayList<Long>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for label: " + label);
        for(Cell match : values) {
            try {
                matching.add(Long.parseLong(match.getValue()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("tuple value for field " +
                        label + " could not be cast to long (" + match + ")");
            }
        }
        return matching;
    }


    public String getStringSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        List<T> values = matchLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(isEmpty(values))
            throw new NoSuchElementException("No matching value for " + field);
        return values.get(0).getValue();
    }

    public double getDoubleSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to double (" + value + ")");
        }
    }

    public int getIntSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to int (" + value + ")");
        }
    }

    public long getLongSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to long (" + value + ")");
        }
    }

    public byte[] getByteSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        return value.getBytes();
    }

    private String internalSingleGet(List<T> tuples, String field) {
        List<T> values = matchLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(isEmpty(values))
            throw new NoSuchElementException("No matching value for " + field);
        return values.get(0).getValue();
    }
}
