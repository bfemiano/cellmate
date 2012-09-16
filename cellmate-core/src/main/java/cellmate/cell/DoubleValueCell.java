package cellmate.cell;

import com.google.common.annotations.Beta;

/**
 * Cell that holds value as double. Not extendable.
 *
 * Fields must be read using Reflection. See {@link cellmate.extractor.CellReflector}
 */
@Beta
@Cell
public class DoubleValueCell {

    @Label
    private String label;

    @Value
    private double value;

    public DoubleValueCell(String label, double value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DoubleValueCell that = (DoubleValueCell) o;

        if (Double.compare(that.value, value) != 0) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = label != null ? label.hashCode() : 0;
        temp = value != +0.0d ? Double.doubleToLongBits(value) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
