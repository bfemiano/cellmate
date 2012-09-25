package cellmate.accumulo.cell;

import cellmate.cell.*;
import com.google.common.annotations.Beta;

/**
 * Cell implementation with a String value and Auxiliary fields
 * to hold ColumnVisibility and Timestamp.
 *
 * ColumnFamily present.
 */
@Beta
@Cell
public final class SecurityStringValueCell{

    @Label
    private String label;

    @Value
    private String value;

    @CellAuxilaryField(name="colvis")
    private String colVis;

    @CellAuxilaryField(name="timestamp")
    private long timestamp;

    @ColumnFamily
    private String colFam;


    public SecurityStringValueCell(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public SecurityStringValueCell(String label, String value, String colFam) {
        this(label, value);
        this.colFam = colFam;
    }

    public SecurityStringValueCell(String label, String value, long timestamp) {
        this(label, value);
        this.timestamp = timestamp;
    }

    public SecurityStringValueCell(String label, String value, long timestamp, String colVis) {
        this(label, value ,timestamp);
        this.colVis = colVis;
    }

    public SecurityStringValueCell(String label, String value, long timestamp, String colVis, String colFam) {
        this(label, value ,timestamp, colVis);
        this.colFam = colFam;
    }
}