package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityIntValueCell;
import cellmate.cell.CellGroup;
import cellmate.reader.CellTransformer;
import com.google.common.base.Defaults;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Cell transformer for int value contents.
 * Qualifer is written as label.</br></br>
 *
 *  Optional flags to ignore column family, column visibility, and timestamp
 *
 * {@link AccumuloCellTransformers}
 */
public class SecurityIntCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> {
    private boolean recordTsAndColVis;
    private boolean recordCF;

    public SecurityIntCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordCF = recordColFam;
    }

    public SecurityIntCellTransformer() {
        recordTsAndColVis = false;
        recordCF = false;
    }

    public CellGroup<SecurityIntValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                 CellGroup<SecurityIntValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityIntValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        byte [] valueBytes = dbItem.getValue().get();
        int value = valueBytes.length > 0 ? ByteBuffer.wrap(valueBytes).asIntBuffer().get() : Defaults.defaultValue(int.class);
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityIntValueCell cell;
        if(recordCF & recordTsAndColVis) {
            cell = new SecurityIntValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordCF) {
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
