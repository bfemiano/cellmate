package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityByteValueCell;
import cellmate.cell.CellGroup;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * Cell transformer for byte[] value contents.
 * Qualifer is written as label.</br></br>
 *
 *  Optional flags to ignore column family, column visibility, and timestamp
 *
 * {@link AccumuloCellTransformers}
 */
public class SecurityByteCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> {
    private boolean recordTsAndColVis;
    private boolean recordCF;

    public SecurityByteCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordCF = recordColFam;
    }

    public SecurityByteCellTransformer() {
        recordTsAndColVis = false;
        recordCF = false;
    }

    /**
     * Convert Key/Value pair to CellGroup or new cell group if new rowId is seen
     * in the Key/Value pair.
     *
     * @param dbItem key/value pair
     * @param cellGroup
     * @return CellGroup
     */
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
        if(recordCF & recordTsAndColVis) {
            cell = new SecurityByteValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordCF) {
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
