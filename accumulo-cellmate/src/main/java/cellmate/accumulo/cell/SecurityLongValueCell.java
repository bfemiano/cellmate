package cellmate.accumulo.cell;

import cellmate.cell.*;
import com.google.common.annotations.Beta;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 2:14 PM
 */
@Beta
@Cell
public class SecurityLongValueCell {

    @Label
    private String label;

    @Value
    private long value;

    @CellAuxilaryField(name="colvis")
    private String colVis;

    @CellAuxilaryField(name="timestamp")
    private long timestamp;

    @ColumnFamily
    private String colFam;

    public SecurityLongValueCell(String label, long value) {
        this.label = label;
        this.value = value;
    }

    public SecurityLongValueCell(String label, long value, String colfam) {
        this(label, value);
        this.colFam = colfam;
    }

    public SecurityLongValueCell(String label, long value, long timestamp) {
        this(label, value);
        this.timestamp = timestamp;
    }

    public SecurityLongValueCell(String label, long value, long timestamp, String colVis) {
        this(label, value ,timestamp);
        this.colVis = colVis;
    }

    public SecurityLongValueCell(String label, long value, long timestamp, String colVis, String colFam) {
        this(label, value ,timestamp, colVis);
        this.colFam = colFam;
    }
}
