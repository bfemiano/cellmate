package cellmate.accumulo.reader;


import cellmate.accumulo.parameters.AccumuloParameterOps;
import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.accumulo.reader.scan.Scan;
import cellmate.accumulo.reader.scan.MultiRangeScan;
import cellmate.accumulo.reader.scan.SingleRangeScan;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.reader.CellTransformer;
import cellmate.reader.DBResultReader;
import cellmate.reader.BasicCellGroupingDBResultReader;
import com.google.common.annotations.VisibleForTesting;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Handles reading Accumulo parameters, performing scans over Accumulo,
 * and using the supplied CellTransformer to build CellGroups to return.
 *
 */
public class AccumuloDBResultReader implements DBResultReader<Map.Entry<Key,Value>> {


    private Instance instance;
    private String instanceName;
    private String zookeepers;
    private static final Logger log = Logger.getLogger(AccumuloDBResultReader.class);
    private DBResultReader<Map.Entry<Key,Value>> baseReader;


    @VisibleForTesting
    public AccumuloDBResultReader(Instance instance){
        this.instance = instance;
        baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>>();
    }

    @VisibleForTesting
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>> baseReader, Instance instance){
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
            this.baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>>();
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
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>> baseReader, AccumuloParameters parameters) {
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
    public AccumuloDBResultReader(DBResultReader<Map.Entry<Key,Value>> baseReader, String instanceName, String zookeepers){
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
        baseReader = new BasicCellGroupingDBResultReader<Map.Entry<Key, Value>>();
    }


    /**
     *  Expects AccumuloParameters. Builds the connection instance and performs the scan
     *  with the supplied parameters.
     *
     * @param params query parameters specific to Accumulo.
     * @param transformer responsible for building cells and cell groups from DB scan results.
     * @param <C> cell type
     * @return list of cell groups generated.
     * @throws IllegalArgumentException if no table present or table not found.
     */
    public <C> List<CellGroup<C>> read(Parameters params, CellTransformer<Map.Entry<Key, Value>, C> transformer) {
        AccumuloParameters parameters = AccumuloParameterOps.checkParamType(params);
        Connector connector = AccumuloParameterOps.getConnectorFromParameters(instance, parameters);
        Authorizations auths = AccumuloParameterOps.getAuthsFromConnector(connector);
        if(log.isDebugEnabled())
            log.info("Create auths and connector for " + parameters.getUser());
        Scan scan = parameters.hasKey(AccumuloParameters.MULTI_RANGE) ?
                new MultiRangeScan(connector, auths, parameters) :
                new SingleRangeScan(connector, auths, parameters);
        if(log.isDebugEnabled())
            log.info("Setting up scan type: " + scan.getClass().getName());
        return read(scan.get(), parameters, transformer);
    }

    /**
     * Delegates to baseReader read(), which by default is {@link BasicCellGroupingDBResultReader}
     * unless otherwise changed by the client at construction.
     *
     * @param dbItems items to scan.
     * @param parameters query scan parameters.
     * @param transformer cell generation function.
     * @param <C> cell type
     * @return  list of cell groups.
     */
    public <C> List<CellGroup<C>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                       Parameters parameters,
                                       CellTransformer<Map.Entry<Key, Value>, C> transformer) {
        return baseReader.read(dbItems, parameters, transformer);
    }
}
