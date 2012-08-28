package cellmate.tuple;

/**
 * User: bfemiano
 * Date: 8/26/12
 * Time: 10:57 AM
 */
public class CellTuple implements Cell {

    private String label;
    private String value;
    private long timestamp;

    public CellTuple(String label, String value, long timestamp) {
        this.label = label;
        this.value = value;
        this.timestamp = timestamp;
    }

    public CellTuple(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CellTuple cellTuple = (CellTuple) o;

        if (timestamp != cellTuple.timestamp) return false;
        if (label != null ? !label.equals(cellTuple.label) : cellTuple.label != null) return false;
        if (value != null ? !value.equals(cellTuple.value) : cellTuple.value != null) return false;

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
