package cellmate.extractor;

import cellmate.exception.NullDataForLabelValueException;
import cellmate.tuple.CellReflector;

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
public class StringMultiSingleValueCellExtractor
        implements CellExtractor {

    public <T> List<T> matchLabel(List<T> tuples, String label) {
        List<T> values = new ArrayList<T>();
        for(T tuple : tuples) {
            try {
                if(CellReflector.getLabelAsString(tuple).equals(label))
                    values.add(tuple);
            } catch (NullDataForLabelValueException e) {
                continue;
            }
        }
        return values;
    }



    public <T> T getMostRecentTimestamp(List<T> tuples) {
        T maxT = null;
        long max = Long.MIN_VALUE;
        for(T t : tuples){
            long ts = (Long)CellReflector.getAuxiliaryValue(t, "ts");
            if(ts> max) {
                maxT = t;
                max = ts;
            }
        }
        return maxT;
    }

    public <T> List<T> regexMatchLabel(List<T> tuples, String regex) {
        List<T> values = new ArrayList<T>();
        for(T tuple : tuples) {
            try {
                if(Pattern.matches(regex, CellReflector.getLabelAsString(tuple)))
                    values.add(tuple);
            } catch (NullDataForLabelValueException e) {
                continue;
            }
        }
        return values;
    }

    public <T> boolean hasMoreThanOne(List<T> values)
            throws IllegalArgumentException{
        return values.size() > 1;
    }

    public <T> boolean isEmpty(List<T> values)
            throws IllegalArgumentException{
        return values.size() == 0;
    }

    public <T> List<Integer> getIntList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<Integer> matching = new ArrayList<Integer>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(T match : values) {
            try {
                matching.add(CellReflector.getValueAsInt(match));
            } catch (NullDataForLabelValueException e) {
                throw new IllegalArgumentException("tuple value for field " +
                        " was empty (" + match + ")");
            }
        }
        return matching;
    }

    public <T> List<String> getStringList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<String> matching = new ArrayList<String>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for regex: " + label);
        for(T match : values) {
            try {
                matching.add(CellReflector.getValueAsString(match));
            } catch (NullDataForLabelValueException e) {
                throw new IllegalArgumentException("tuple value for field " +
                        " was empty (" + match + ")");
            }
        }
        return matching;
    }

    public <T> List<Long> getLongList(List<T> internalList, String label)
            throws NoSuchElementException, IllegalArgumentException{
        List<T> values = matchLabel(internalList, label);
        List<Long> matching = new ArrayList<Long>();
        if(isEmpty(values))
            throw new NoSuchElementException("No matching values found for label: " + label);
        for(T match : values) {
            try {
                matching.add(CellReflector.getValueAsLong(match));
            } catch (NullDataForLabelValueException e) {
                throw new IllegalArgumentException("cell value was null " +
                        "(" + match + ")");
            }
        }
        return matching;
    }


    public <T> String getStringSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        List<T> values = matchLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(isEmpty(values))
            throw new NoSuchElementException("No matching value for " + field);
        try {
            return CellReflector.getValueAsString(values.get(0));
        } catch (NullDataForLabelValueException e) {
            throw new IllegalArgumentException("cell value was null", e);
        }
    }

    public <T> double getDoubleSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to double (" + value + ")");
        }
    }

    public <T> int getIntSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to int (" + value + ")");
        }
    }

    public <T> long getLongSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("tuple value for field " +
                    field + " could not be cast to long (" + value + ")");
        }
    }

    public <T> byte[] getByteSingleValue(List<T> tuples, String field)
            throws IllegalArgumentException, NoSuchElementException {
        String value = internalSingleGet(tuples, field);
        return value.getBytes();
    }

    private <T> String internalSingleGet(List<T> tuples, String field) {
        List<T> values = matchLabel(tuples, field);
        if(hasMoreThanOne(values))
            throw new IllegalArgumentException("Too many matching values for " + field);
        if(isEmpty(values))
            throw new NoSuchElementException("No matching value for " + field);
        try {
            return CellReflector.getValueAsString(values.get(0));
        } catch (NullDataForLabelValueException e) {
            throw new IllegalArgumentException("cell value was null", e);
        }
    }
}
