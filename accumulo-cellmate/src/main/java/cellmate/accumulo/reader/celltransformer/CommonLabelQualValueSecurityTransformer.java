package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 1:46 AM
 */
public class CommonLabelQualValueSecurityTransformer
        implements CellTransformer<Map.Entry<Key,Value>,SecurityStringValueCell>{

    private Map<String, String> colFamToCommonLabel;

    public CommonLabelQualValueSecurityTransformer(Map<String, String> colFamToCommonLabel) {
        this.colFamToCommonLabel = colFamToCommonLabel;
    }

    public Tuple<SecurityStringValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                Tuple<SecurityStringValueCell> tuple) {
        String activeRowId = dbItem.getKey().getRow().toString();
        if (tuple == null) {
            tuple = new Tuple<SecurityStringValueCell>(activeRowId);
        } else if (!tuple.getTag().equals(activeRowId)) {
            tuple = new Tuple<SecurityStringValueCell>(activeRowId);
        }
        String colFamStr = dbItem.getKey().getColumnFamily().toString();
        String label = dbItem.getKey().getColumnQualifier().toString();
        String value = new String(dbItem.getValue().get());
        if(colFamToCommonLabel.containsKey(colFamStr)){
            value = label;
            label = colFamToCommonLabel.get(colFamStr);
        }
        SecurityStringValueCell cell = new SecurityStringValueCell(label, value);
        tuple.addCell(cell);
        return tuple;
    }
}
