package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityLongValueCell;
import cellmate.cell.CellGroup;
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

    private boolean recordTsAndColVis;
    private boolean recordColFamilies;

    public SecurityLongCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordColFamilies = recordColFam;
    }

    public SecurityLongCellTransformer() {
        recordTsAndColVis = false;
        recordColFamilies = false;
    }

    public CellGroup<SecurityLongValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                CellGroup<SecurityLongValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityLongValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        long value = ByteBuffer.wrap(dbItem.getValue().get()).asLongBuffer().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityLongValueCell cell;
        if(recordColFamilies & recordTsAndColVis) {
            cell = new SecurityLongValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFamilies) {
            cell = new SecurityLongValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityLongValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityLongValueCell(label, value);
        }
        cellGroup.addCell(cell);
        return cellGroup;
    }
}
