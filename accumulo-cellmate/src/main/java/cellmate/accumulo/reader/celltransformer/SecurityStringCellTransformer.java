package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.cell.CellGroup;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 1:15 AM
 */
public class SecurityStringCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell>{

    private boolean recordTsAndColVis = false;
    private boolean recordColFam = false;

    public SecurityStringCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        this.recordTsAndColVis = recordTimestampAndColVis;
        this.recordColFam = recordColFam;
    }

    public CellGroup<SecurityStringValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                CellGroup<SecurityStringValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityStringValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        String value = new String(dbItem.getValue().get());
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityStringValueCell cell;
        if(recordColFam & recordTsAndColVis) {
            cell = new SecurityStringValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFam) {
            cell = new SecurityStringValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityStringValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityStringValueCell(label, value);
        }
        cellGroup.addCell(cell);
        return cellGroup;
    }

}
