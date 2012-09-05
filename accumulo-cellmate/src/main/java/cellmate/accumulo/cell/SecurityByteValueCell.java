package cellmate.accumulo.cell;

import cellmate.cell.*;
import com.google.common.annotations.Beta;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 1:51 PM
 */
@Beta
@Cell
public class SecurityByteValueCell {

    @Label
    private String label;

    @Value
    private byte[] value;

    @CellAuxilaryField(name="colvis")
    private String colVis;

    @CellAuxilaryField(name="timestamp")
    private long timestamp;

    @ColumnFamily
    private String colFam;

    public SecurityByteValueCell(String label, byte[] value) {
        this.label = label;
        this.value = value;
    }

    public SecurityByteValueCell(String label, byte[] value, String colfam) {
        this(label, value);
        this.colFam = colfam;
    }

    public SecurityByteValueCell(String label, byte[] value, long timestamp) {
        this(label, value);
        this.timestamp = timestamp;
    }

    public SecurityByteValueCell(String label, byte[] value, long timestamp, String colVis) {
        this(label, value ,timestamp);
        this.colVis = colVis;
    }

    public SecurityByteValueCell(String label, byte[] value, long timestamp, String colVis, String colFam) {
        this(label, value ,timestamp, colVis);
        this.colFam = colFam;
    }

}
