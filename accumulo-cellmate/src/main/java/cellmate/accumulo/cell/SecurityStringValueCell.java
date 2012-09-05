package cellmate.accumulo.cell;

import cellmate.cell.*;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 1:04 AM
 */
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

    public SecurityStringValueCell(String label, String value, String colVis) {
        this(label, value);
        this.colVis = colVis;
    }

    public SecurityStringValueCell(String label, String value, long timestamp) {
        this(label, value);
        this.timestamp = timestamp;
    }

    public SecurityStringValueCell(String label, String value, long timestamp, String colVis) {
        this(label, value ,timestamp);
        this.colVis = colVis;
    }

    public void setColFam(String colFam){
        this.colFam = colFam;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setColVis(String colVis) {
        this.colVis = colVis;
    }
}