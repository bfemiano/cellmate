package cellmate.accumulo.cell;

import cellmate.cell.*;
import com.google.common.annotations.Beta;

/**
 * Cell implementation with a int value and Auxiliary fields
 * to hold ColumnVisibility and Timestamp.
 *
 * ColumnFamily present.
 */
@Beta
@Cell
public final class SecurityIntValueCell {

    @Label
    private String label;

    @Value
    private int value;

    @CellAuxilaryField(name="colvis")
    private String colVis;

    @CellAuxilaryField(name="timestamp")
    private long timestamp;

    @ColumnFamily
    private String colFam;

    public SecurityIntValueCell(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public SecurityIntValueCell(String label, int value, String colFam) {
        this(label, value);
        this.colFam = colFam;
    }

    public SecurityIntValueCell(String label, int value, long timestamp) {
        this(label, value);
        this.timestamp = timestamp;
    }

    public SecurityIntValueCell(String label, int value, long timestamp, String colVis) {
        this(label, value ,timestamp);
        this.colVis = colVis;
    }

    public SecurityIntValueCell(String label, int value, long timestamp, String colVis, String colFam) {
        this(label, value ,timestamp, colVis);
        this.colFam = colFam;
    }
}
