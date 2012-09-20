package cellmate.accumulo.reader.scan;

import cellmate.accumulo.parameters.AccumuloParameterOps;
import cellmate.accumulo.parameters.AccumuloParameters;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Multiple start-end key ranges. Uses the Accumulo Batch Scanner.
 *
 * See #getRangesFromParameters in {@link AccumuloParameterOps}
 */
public class MultiRangeScan extends AccumuloScan {

    private BatchScanner scan;

    public MultiRangeScan(Connector conn, Authorizations auths, AccumuloParameters params) {
        super(conn, auths, params);
        init();
    }

    private void init() {
        try {
            this.scan = conn.createBatchScanner(params.getTableName(), auths, params.getNumQueryThreads());
            scan.setRanges(AccumuloParameterOps.getRangesFromParameters(params));
            scan = (BatchScanner) attachIterators(scan, params);
            scan = (BatchScanner) addColFamsAndQuals(scan ,params);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Missing table name in parameters");
        } catch (TableNotFoundException e) {
            throw new IllegalArgumentException("Table not found during read: " + params.getTableName(),e);
        }
    }

    public Iterable<Map.Entry<Key, Value>> get() {
        return scan;
    }
}
