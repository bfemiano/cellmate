package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityByteValueCell;
import cellmate.cell.CellGroup;
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
    private boolean recordTsAndColVis;
    private boolean recordColFamilies;

    public SecurityByteCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordColFamilies = recordColFam;
    }

    public SecurityByteCellTransformer() {
         recordTsAndColVis = false;
        recordColFamilies = false;
    }

    public CellGroup<SecurityByteValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                CellGroup<SecurityByteValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityByteValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        byte[] value = dbItem.getValue().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityByteValueCell cell;
        if(recordColFamilies & recordTsAndColVis) {
            cell = new SecurityByteValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFamilies) {
            cell = new SecurityByteValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityByteValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityByteValueCell(label, value);
        }
        cellGroup.addCell(cell);
        return cellGroup;
    }
}
