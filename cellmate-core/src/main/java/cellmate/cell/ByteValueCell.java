package cellmate.cell;

import com.google.common.annotations.Beta;

import java.util.Arrays;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 9:24 PM
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
        this.value = value;
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
