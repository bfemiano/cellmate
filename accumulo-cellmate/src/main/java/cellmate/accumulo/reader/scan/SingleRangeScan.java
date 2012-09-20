package cellmate.accumulo.reader.scan;

import cellmate.accumulo.parameters.AccumuloParameters;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.ScannerBase;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Single start-key range. Wraps the Accumulo Scanner.
 */
public class SingleRangeScan extends AccumuloScan {


    private static final Logger log = Logger.getLogger(SingleRangeScan.class);
    private Scanner scan;

    public SingleRangeScan(Connector conn, Authorizations auths, AccumuloParameters params) {
        super(conn,  auths, params);
        init();
    }

    private void init() {
        try {
            scan = conn.createScanner(params.getTableName(), auths);
            scan = setBatchSize(scan, params);
            scan = addRange(scan, params);
            scan = (Scanner) attachIterators(scan, params);
            scan = (Scanner) addColFamsAndQuals(scan ,params);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Missing table name in parameters");
        } catch (TableNotFoundException e) {
            throw new IllegalArgumentException("Table not found during read: " + params.getTableName(),e);
        }
    }

    private Scanner setBatchSize(Scanner scan, AccumuloParameters parameters) {
        try {
            int batchSize = parameters.getBatchSize();
            scan.setBatchSize(batchSize);
        } catch (NoSuchElementException e){
            if(log.isInfoEnabled())
                log.info("Using default batch size of " + scan.getBatchSize());
        }
        return scan;
    }


    private Scanner addRange(Scanner scan, AccumuloParameters parameters) {
        String startKey;
        String endKey;
        try {
            startKey = parameters.getStartKey();
        } catch (NoSuchElementException e){
            startKey = null;
        }

        try {
            endKey = parameters.getEndKey();
        } catch (NoSuchElementException e){
            endKey = null;
        }
        if(log.isInfoEnabled())
            log.info("SingleRangeScan using start key: " + startKey + " end key: " + endKey);
        scan.setRange(new Range(startKey, endKey));
        return scan;
    }

    public ScannerBase get() {
        return scan;
    }
}
