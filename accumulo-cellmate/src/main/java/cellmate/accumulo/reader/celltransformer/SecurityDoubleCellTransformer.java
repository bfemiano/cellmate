package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityDoubleValueCell;
import cellmate.cell.CellGroup;
import cellmate.reader.CellTransformer;
import com.google.common.base.Defaults;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/12/12
 * Time: 12:03 AM
 */
public class SecurityDoubleCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> {

    private boolean recordTsAndColVis;
    private boolean recordCF;

    public SecurityDoubleCellTransformer(boolean recordTimestampAndColVis, boolean recordColFam){
        recordTsAndColVis = recordTimestampAndColVis;
        recordCF = recordColFam;
    }

    public SecurityDoubleCellTransformer() {
        recordTsAndColVis = false;
        recordCF = false;
    }

    public CellGroup<SecurityDoubleValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                    CellGroup<SecurityDoubleValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityDoubleValueCell>(activeRowId);
        }
        String label = dbItem.getKey().getColumnQualifier().toString();
        byte [] valueBytes = dbItem.getValue().get();
        ByteBuffer buffer = ByteBuffer.wrap(valueBytes);
        DoubleBuffer dbBuffer = buffer.asDoubleBuffer();
        double value = valueBytes.length > 0 ? dbBuffer.get() : Defaults.defaultValue(double.class);
        String colVis = dbItem.getKey().getColumnVisibility().toString();
        String colFam = dbItem.getKey().getColumnFamily().toString();
        long timestamp = dbItem.getKey().getTimestamp();
        SecurityDoubleValueCell cell;
        if(recordCF & recordTsAndColVis) {
            cell = new SecurityDoubleValueCell(label, value, timestamp, colVis, colFam);
        } else if (recordCF) {
            cell = new SecurityDoubleValueCell(label, value, colFam);
        } else if (recordTsAndColVis) {
            cell = new SecurityDoubleValueCell(label, value, timestamp, colVis);
        } else {
            cell = new SecurityDoubleValueCell(label, value);
        }
        cellGroup.addCell(cell);
        return cellGroup;
    }
}
