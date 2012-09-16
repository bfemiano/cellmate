package cellmate.accumulo.reader;


import cellmate.accumulo.parameters.AccumuloParameterOps;
import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.reader.CellTransformer;
import cellmate.reader.DBResultReader;
import cellmate.reader.BasicCellGroupingDBResultReader;
import com.google.common.annotations.VisibleForTesting;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * Handles reading Accumulo parameters, performing scans over Accumulo,
 * and using the supplied CellTransformer to build CellGroups to return.
 *
 *
 * @param <C> cell type.
 */
public class AccumuloDBResultReader<C>
        implements DBResultReader<Map.Entry<Key,Value>,C> {


    private Instance instance;
    private String instanceName;
    private String zookeepers;
    private static final Logger log = Logger.getLogger(AccumuloDBResultReader.class);
    private static final Pattern colon = Pattern.compile("[:]");
    private DBResultReader<Map.Entry<Key,Value>, C> baseReader;


    @VisibleForTesting
    public AccumuloDBResultReader(Instance instance){
        this.instance = instance;
        baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>, C>();
    }

    @VisibleForTesting
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>, C> baseReader, Instance instance){
        this.instance = instance;
        this.baseReader = baseReader;
    }

    /**
     * Constructor
     *
     * builds an Accumulo instance by expecting to find the instance name and comma-delimted zookepers in the parameter list.
     * Defaults the delegate reader to the {@link BasicCellGroupingDBResultReader}
     *
     * @param parameters
     */
    public AccumuloDBResultReader(AccumuloParameters parameters){
        try {
            String instanceName = parameters.getInstanceName();
            String zookeepers = parameters.getZookeepers();
            this.instance = new ZooKeeperInstance(instanceName, zookeepers);
            this.baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>, C>();
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing zookeepers and/or instance id");
        }
    }

    /**
     *  Constructor
     *
     * @param baseReader injectable reader instance to delegte operations to.
     * @param parameters scan parameters.
     */
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>, C> baseReader, AccumuloParameters parameters) {
        this(parameters);
        this.baseReader = baseReader;
    }

    /**
     *  Constructor
     *
     * @param baseReader injectable reader instance to delegte operations to.
     * @param instanceName Accumulo instance
     * @param zookeepers comma-delimited zookeeper list.
     */
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>, C> baseReader, String instanceName, String zookeepers){
        instance = new ZooKeeperInstance(instanceName, zookeepers);
        this.baseReader = baseReader;
    }

    /**
     * Constructor
     *
     * @param instanceName Accumulo instance name.
     * @param zookeepers comma-delimited list.
     */
    public AccumuloDBResultReader(String instanceName, String zookeepers){
        instance = new ZooKeeperInstance(instanceName, zookeepers);
        baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>, C>();
    }


    /**
     *  Expects AccumuloParameters. Builds the connection instance and performs the scan
     *  with the supplied parameters.
     *
     * @param params query parameters specific to Accumulo.
     * @param transformer responsible for building cells and cell groups from DB scan results.
     * @return list of cell groups generated.
     * @throws IllegalArgumentException if no table present or table not found.
     */
    public List<CellGroup<C>> read(Parameters params, CellTransformer<Map.Entry<Key, Value>, C> transformer) {
        AccumuloParameters parameters = AccumuloParameterOps.checkParamType(params);
        Connector connector = AccumuloParameterOps.getConnectorFromParameters(instance, parameters);
        Authorizations auths = AccumuloParameterOps.getAuthsFromConnector(connector);
        try {
            Scanner scan = connector.createScanner(parameters.getTableName(), auths);
            scan = addRange(scan, parameters);
            scan = addColFamsAndQuals(scan, parameters);
            scan = setBatchSize(scan, parameters);
            scan = attachIterators(scan, parameters);
            return read(scan, parameters, transformer);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("Missing table name in parameters");
        } catch (TableNotFoundException e) {
            throw new IllegalArgumentException("Table not found during read: " + parameters.getTableName(),e);
        }
    }

    private Scanner attachIterators(Scanner scan, AccumuloParameters parameters) {
        for(IteratorSetting iterator : parameters.getIterators())
            scan.addScanIterator(iterator);
        return scan;
    }

    /**
     * Delegates to baseReader read(), which by default is {@link BasicCellGroupingDBResultReader}
     * unless otherwise changed by the client at construction.
     *
     * @param dbItems items to scan.
     * @param parameters query scan parameters.
     * @param transformer cell generation function.
     * @return  list of cell groups.
     */
    public List<CellGroup<C>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                   Parameters parameters,
                                   CellTransformer<Map.Entry<Key, Value>, C> transformer) {
        return baseReader.read(dbItems, parameters, transformer);
    }

    private Scanner addColFamsAndQuals(Scanner scan, AccumuloParameters parameters) {
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

    private Scanner setBatchSize(Scanner scan, AccumuloParameters parameters) {
        try {
            int batchSize = parameters.getBatchSize();
            scan.setBatchSize(batchSize);
        } catch (NoSuchElementException e){
            if(log.isInfoEnabled())
                log.info(" Using default batch size.");
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

        scan.setRange(new Range(startKey, endKey));
        return scan;
    }
}
