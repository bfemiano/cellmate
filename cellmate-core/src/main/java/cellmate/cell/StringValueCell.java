package cellmate.cell;

import com.google.common.annotations.Beta;

/**
 *
 * Holds its value as a String instance. Not extendable.
 *
 * Fields must be read using Reflection. See {@link cellmate.extractor.CellReflector}
 *
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

    @ColumnFamily
    private String colFam;

    public StringValueCell(String label, String value, long timestamp) {
        this.label = label;
        this.value = value;
        this.timestamp = timestamp;
    }

    public StringValueCell(String label, String value, String colFam, long timestamp) {
        this.label = label;
        this.value = value;
        this.colFam = colFam;
        this.timestamp = timestamp;
    }

    public StringValueCell(String label, String value, String colFam) {
        this.label = label;
        this.value = value;
        this.colFam = colFam;
    }

    public StringValueCell(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StringValueCell that = (StringValueCell) o;

        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = label != null ? label.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
