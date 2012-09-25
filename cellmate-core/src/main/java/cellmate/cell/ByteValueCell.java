package cellmate.cell;

import com.google.common.annotations.Beta;

import java.util.Arrays;

/**
 * Holds its value as a byte array. Not extendable.
 *
 * Fields must be read using Reflection. See {@link cellmate.extractor.CellReflector}
 */
@Beta
@Cell
public final class ByteValueCell {

    @Label
    private String label;

    @Value
    private byte[] value;

    public ByteValueCell(String label, byte[] value) {
        this.label = label;
        this.value = value.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ByteValueCell that = (ByteValueCell) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (!Arrays.equals(value, that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (value != null ? Arrays.hashCode(value) : 0);
        return result;
    }
}
