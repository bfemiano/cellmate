package cellmate.tuple;

import cellmate.tuple.cell.Cell;
import cellmate.tuple.cell.CellAuxilaryField;
import cellmate.tuple.cell.Label;
import cellmate.tuple.cell.Value;

/**
 * User: bfemiano
 * Date: 8/26/12
 * Time: 10:57 AM
 */
@Cell
public final class StringValueTuple {

    @Label
    public String label;

    @Value
    private String value;

    @CellAuxilaryField(name="ts")
    private long timestamp;

    public StringValueTuple(String label, String value, long timestamp) {
        this.label = label;
        this.value = value;
        this.timestamp = timestamp;
    }

    public StringValueTuple(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringValueTuple stringValueTuple = (StringValueTuple) o;

        if (timestamp != stringValueTuple.timestamp) return false;
        if (label != null ? !label.equals(stringValueTuple.label) : stringValueTuple.label != null) return false;
        if (value != null ? !value.equals(stringValueTuple.value) : stringValueTuple.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
