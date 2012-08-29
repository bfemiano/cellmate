package cellmate.tuple;

import cellmate.tuple.cell.Cell;
import cellmate.tuple.cell.Label;
import cellmate.tuple.cell.Value;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 9:23 PM
 */
@Cell
public final class LongValueTuple {

    @Label
    private String label;

    @Value
    private long value;

    public LongValueTuple(String label, long value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongValueTuple that = (LongValueTuple) o;

        if (value != that.value) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (int) (value ^ (value >>> 32));
        return result;
    }
}
