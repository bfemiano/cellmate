package cellmate.accumulo.reader;

import cellmate.cell.StringValueCell;
import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import cellmate.reader.DBResultReader;
import cellmate.reader.ReadParameters;
import com.google.common.collect.ImmutableList;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.Map;

/**
 * User: bfemiano
 * Date: 9/5/12
 * Time: 12:46 AM
 */
public class AccumuloStringValueCellTransformingReader
        implements DBResultReader<Map.Entry<Key,Value>,StringValueCell>{

    private AccumuloTransformeringDBResultReader<StringValueCell> reader;

    public AccumuloStringValueCellTransformingReader(AccumuloReadParameters parameters) {
        reader = new AccumuloTransformeringDBResultReader<StringValueCell>(parameters);
    }

    public ImmutableList<Tuple<StringValueCell>> read(Iterable<Map.Entry<Key, Value>> dbItems, ReadParameters parameters) {
        return reader.read(dbItems, parameters, new CellTransformer<Map.Entry<Key, Value>, StringValueCell>() {
            public Tuple<StringValueCell> apply(Map.Entry<Key, Value> dbItem, Tuple<StringValueCell> tuple) {
                String activeRowId = dbItem.getKey().getRow().toString();
                if (tuple == null) {
                    return new Tuple<StringValueCell>(activeRowId);
                } else if (!tuple.getTag().equals(activeRowId)) {
                    return new Tuple<StringValueCell>(activeRowId);
                }
                String label = dbItem.getKey().getColumnQualifier().toString();
                String value = new String(dbItem.getValue().get());
                StringValueCell cell = new StringValueCell(label, value);
                tuple.addCell(cell);
                return tuple;
            }
        });
    }

    public ImmutableList<Tuple<StringValueCell>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                                      ReadParameters parameters,
                                                      CellTransformer<Map.Entry<Key, Value>, StringValueCell> transformer) {
       return reader.read(dbItems, parameters, transformer);
    }
}
