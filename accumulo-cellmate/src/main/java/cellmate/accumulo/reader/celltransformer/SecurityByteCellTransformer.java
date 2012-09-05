package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityByteValueCell;
import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 2:24 PM
 */
public class SecurityByteCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> {
    private boolean recordTsAndColVis = false;
    private boolean recordColFam = false;

    public SecurityByteCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        this.recordTsAndColVis = recordTimestampAndColVis;
        this.recordColFam = recordColFam;
    }

    public Tuple<SecurityByteValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                Tuple<SecurityByteValueCell> tuple) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (tuple == null) {
            tuple = new Tuple<SecurityByteValueCell>(activeRowId);
        } else if (!tuple.getTag().equals(activeRowId)) {
            tuple = new Tuple<SecurityByteValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        byte[] value = dbItem.getValue().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityByteValueCell cell;
        if(recordColFam & recordTsAndColVis) {
            cell = new SecurityByteValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFam) {
            cell = new SecurityByteValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityByteValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityByteValueCell(label, value);
        }
        tuple.addCell(cell);
        return tuple;
    }
}
