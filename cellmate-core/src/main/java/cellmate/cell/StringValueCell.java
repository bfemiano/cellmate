package cellmate.cell;

import com.google.common.annotations.Beta;

import java.util.Arrays;

/**
 * User: bfemiano
 * Date: 8/26/12
 * Time: 10:57 AM
 */
@Beta
@Cell
public final class StringValueCell {

    @Label
    public String label;

    @Value
    private String value;

    @CellAuxilaryField(name="ts")
    private long timestamp;

    public StringValueCell(String label, String value, long timestamp) {
        this.label = label;
        this.value = value;
        this.timestamp = timestamp;
    }

    public StringValueCell(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringValueCell cell = (StringValueCell) o;

        if (timestamp != cell.timestamp) return false;
        if (label != null ? !label.equals(cell.label) : cell.label != null) return false;
        if (value != null ? !value.equals(cell.value) : cell.value != null) return false;

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
