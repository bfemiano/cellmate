package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.cell.CellGroup;
import cellmate.reader.CellTransformer;
import com.google.common.base.Defaults;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * Cell transformer for String value contents.
 * Qualifer is written as label.</br></br>
 *
 *  Optional flags to ignore column family, column visibility, and timestamp
 *
 * {@link AccumuloCellTransformers}
 */
public class SecurityStringCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell>{

    private boolean recordTsAndColVis;
    private boolean recordCF;

    public SecurityStringCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordCF = recordColFam;
    }

    public SecurityStringCellTransformer() {
        recordTsAndColVis = false;
        recordCF = false;
    }

    public CellGroup<SecurityStringValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                CellGroup<SecurityStringValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityStringValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        byte [] valueBytes = dbItem.getValue().get();
        String value = valueBytes.length > 0 ? new String(valueBytes) : Defaults.defaultValue(String.class);
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityStringValueCell cell;
        if(recordCF & recordTsAndColVis) {
            cell = new SecurityStringValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordCF) {
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
