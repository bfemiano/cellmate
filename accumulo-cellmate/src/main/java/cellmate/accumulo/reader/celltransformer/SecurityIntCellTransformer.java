package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityIntValueCell;
import cellmate.cell.CellGroup;
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
    private boolean recordTsAndColVis;
    private boolean recordColFamilies;

    public SecurityIntCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordColFamilies = recordColFam;
    }

    public SecurityIntCellTransformer() {
        recordTsAndColVis = false;
        recordColFamilies = false;
    }

    public CellGroup<SecurityIntValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                 CellGroup<SecurityIntValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityIntValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        int value = ByteBuffer.wrap(dbItem.getValue().get()).asIntBuffer().get();
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityIntValueCell cell;
        if(recordColFamilies & recordTsAndColVis) {
            cell = new SecurityIntValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordColFamilies) {
            cell = new SecurityIntValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityIntValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityIntValueCell(label, value);
        }
        cellGroup.addCell(cell);
        return cellGroup;
    }
}
