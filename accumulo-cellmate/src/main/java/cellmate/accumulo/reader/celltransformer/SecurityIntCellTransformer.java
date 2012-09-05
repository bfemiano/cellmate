package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityByteValueCell;
import cellmate.accumulo.cell.SecurityIntValueCell;
import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 2:59 PM
 */
public class SecurityIntCellTransformer
           implements CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> {
    private boolean recordTsAndColVis = false;
    private boolean recordColFam = false;

    public SecurityIntCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        this.recordTsAndColVis = recordTimestampAndColVis;
        this.recordColFam = recordColFam;
    }

    public Tuple<SecurityIntValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                Tuple<SecurityIntValueCell> tuple) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (tuple == null) {
            tuple = new Tuple<SecurityIntValueCell>(activeRowId);
        } else if (!tuple.getTag().equals(activeRowId)) {
            tuple = new Tuple<SecurityIntValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        int value = ByteBuffer.wrap(dbItem.getValue().get()).asIntBuffer().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityIntValueCell cell;
        if(recordColFam & recordTsAndColVis) {
            cell = new SecurityIntValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFam) {
            cell = new SecurityIntValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityIntValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityIntValueCell(label, value);
        }
        tuple.addCell(cell);
        return tuple;
    }
}
