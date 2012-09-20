package cellmate.accumulo.reader.scan;

import cellmate.accumulo.parameters.AccumuloParameters;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 *
 * Base class for {@link SingleRangeScan} and {@link MultiRangeScan} where
 * any operations common to ScannerBase can occur.
 */
public abstract class AccumuloScan implements Scan {

    protected Connector conn;
    protected Authorizations auths;
    protected AccumuloParameters params;
    private static final Pattern colon = Pattern.compile("[:]");
    private static final Logger log = Logger.getLogger(AccumuloScan.class);

    public AccumuloScan(Connector conn, Authorizations auths, AccumuloParameters params) {
        this.conn = conn;
        this.auths = auths;
        this.params = params;
    }

    protected ScannerBase attachIterators(ScannerBase scan, AccumuloParameters parameters) {
        for(IteratorSetting iterator : parameters.getIterators())
            scan.addScanIterator(iterator);
        return scan;
    }

    protected ScannerBase addColFamsAndQuals(ScannerBase scan, AccumuloParameters parameters) {
        String[] colfamsAndQuals = parameters.getColumns();
        for(String pair : colfamsAndQuals) {
            String[] colFamAndQual = colon.split(pair);
            if(colFamAndQual.length == 1){
                scan.fetchColumnFamily(new Text(colFamAndQual[0]));
            } else if(colFamAndQual.length == 2){
                scan.fetchColumn(new Text(colFamAndQual[0]), new Text(colFamAndQual[1]));
            } else {
                throw new IllegalArgumentException("malformed colfam entry: " + pair);
            }
        }
        if(colfamsAndQuals.length == 0 && log.isDebugEnabled())
            log.debug("no columns specified in parameters");
        return scan;
    }
}
