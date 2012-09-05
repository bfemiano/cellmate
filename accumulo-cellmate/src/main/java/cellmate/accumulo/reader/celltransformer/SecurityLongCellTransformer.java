package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityLongValueCell;
import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 3:06 PM
 */
public class SecurityLongCellTransformer implements CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> {

    private boolean recordTsAndColVis = false;
    private boolean recordColFam = false;

    public SecurityLongCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        this.recordTsAndColVis = recordTimestampAndColVis;
        this.recordColFam = recordColFam;
    }

    public Tuple<SecurityLongValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                Tuple<SecurityLongValueCell> tuple) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (tuple == null) {
            tuple = new Tuple<SecurityLongValueCell>(activeRowId);
        } else if (!tuple.getTag().equals(activeRowId)) {
            tuple = new Tuple<SecurityLongValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        long value = ByteBuffer.wrap(dbItem.getValue().get()).asLongBuffer().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityLongValueCell cell;
        if(recordColFam & recordTsAndColVis) {
            cell = new SecurityLongValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFam) {
            cell = new SecurityLongValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityLongValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityLongValueCell(label, value);
        }
        tuple.addCell(cell);
        return tuple;
    }
}
