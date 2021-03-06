package cellmate.cell;

import com.google.common.annotations.Beta;

/**
 * Holds its content as long. Not extendable.
 *
 * Fields must be read using Reflection. See {@link cellmate.extractor.CellReflector}
 */
@Beta
@Cell
public final class LongValueCell {

    @Label
    private String label;

    @Value
    private long value;

    public LongValueCell(String label, long value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LongValueCell that = (LongValueCell) o;

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
