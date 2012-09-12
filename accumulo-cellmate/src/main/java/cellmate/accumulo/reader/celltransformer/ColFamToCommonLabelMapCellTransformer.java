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
 * Time: 1:46 AM
 */
public class ColFamToCommonLabelMapCellTransformer
        implements CellTransformer<Map.Entry<Key,Value>,SecurityStringValueCell>{

    private Map<String, String> colFamToCommonLabel;

    public ColFamToCommonLabelMapCellTransformer(Map<String, String> colFamToCommonLabel) {
        this.colFamToCommonLabel = colFamToCommonLabel;
    }

    public CellGroup<SecurityStringValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                CellGroup<SecurityStringValueCell> cellGroup) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (!cellGroup.getTag().equals(activeRowId)) {
            cellGroup = new CellGroup<SecurityStringValueCell>(activeRowId);
        }
        String colFamStr = dbItem.getKey().getColumnFamily().toString();
        String label = dbItem.getKey().getColumnQualifier().toString();
        String value = new String(dbItem.getValue().get());
        if(colFamToCommonLabel.containsKey(colFamStr)){
            value = label;
            label = colFamToCommonLabel.get(colFamStr);
        }
        SecurityStringValueCell cell = new SecurityStringValueCell(label, value, colFamStr);
        cellGroup.addCell(cell);
        return cellGroup;
    }
}
