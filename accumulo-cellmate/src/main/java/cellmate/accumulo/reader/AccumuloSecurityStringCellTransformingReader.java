package cellmate.accumulo.reader;

import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.accumulo.reader.celltransformer.SecurityStringCellTransformer;
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
 * Time: 1:01 AM
 */
public class AccumuloSecurityStringCellTransformingReader
        implements DBResultReader<Map.Entry<Key,Value>,SecurityStringValueCell> {

    private AccumuloTransformeringDBResultReader<SecurityStringValueCell> reader;

    public AccumuloSecurityStringCellTransformingReader(AccumuloReadParameters parameters) {
        reader = new AccumuloTransformeringDBResultReader<SecurityStringValueCell>(parameters);
    }

    public ImmutableList<Tuple<SecurityStringValueCell>> read(Iterable<Map.Entry<Key, Value>> dbItems, ReadParameters parameters) {
        return reader.read(dbItems, parameters, new SecurityStringCellTransformer(false, false, false)); //ignores timestamp, colvis, and col fam.
    }

    public ImmutableList<Tuple<SecurityStringValueCell>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                                      ReadParameters parameters,
                                                      CellTransformer<Map.Entry<Key, Value>, SecurityStringValueCell> transformer) {
       return reader.read(dbItems, parameters, transformer);
    }
}
