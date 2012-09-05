package cellmate.accumulo.reader;


import cellmate.cell.Tuple;
import cellmate.reader.CellTransformer;
import cellmate.reader.DBResultReader;
import cellmate.reader.ReadParameters;
import cellmate.reader.TupleTransformerDBResultReader;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 11:21 PM
 */
public class AccumuloDBResultReader<C>
        implements DBResultReader<Map.Entry<Key,Value>,C> {


    private Instance instance;
    private static final Logger log = Logger.getLogger(AccumuloDBResultReader.class);
    private static final Pattern colon = Pattern.compile("[:]");
    private TupleTransformerDBResultReader<Map.Entry<Key,Value>, C> baseReader = new
            TupleTransformerDBResultReader<Map.Entry<Key, Value>, C>();

    public AccumuloDBResultReader(AccumuloReadParameters parameters){
        try {
            String zoo = parameters.getZookeepers();
            String instanceName = parameters.getInstanceName();
            instance = new ZooKeeperInstance(instanceName, zoo);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing zookeepers and/or instance id");
        }

    }

    @VisibleForTesting
    public AccumuloDBResultReader(AccumuloReadParameters parameters, Instance instance){
        this.instance = instance;
    }

    public ImmutableList<Tuple<C>> read(Iterable<Map.Entry<Key, Value>> dbItems, ReadParameters parameters) {
        return baseReader.read(dbItems, parameters);
    }

    private Authorizations getAuthsFromParameters(Connector connector) {
        try {
            return connector.securityOperations().getUserAuthorizations(connector.whoami());
        } catch (AccumuloException e) {
            throw new RuntimeException("General Accumulo error getting auths for current user: " + connector.whoami(),e);
        } catch (AccumuloSecurityException e) {
            throw new RuntimeException("Security error getting auths for current user: " + connector.whoami(),e);
        }
    }

    public ImmutableList<Tuple<C>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                        ReadParameters params,
                                        CellTransformer<Map.Entry<Key,Value>, C> transformer) {
        if(!(params instanceof AccumuloReadParameters)){
            throw new IllegalArgumentException("ReadParameter implementation must be " + AccumuloReadParameters.class.getName());
        }
        AccumuloReadParameters parameters =  (AccumuloReadParameters)params;
        Connector connector = getConnectorFromParameters(parameters);
        Authorizations auths = getAuthsFromParameters(connector);
        try {
            Scanner scan = connector.createScanner(parameters.getTableName(), auths);
            scan = addRange(scan, parameters);
            scan = addColFamsAndQuals(scan, parameters);
            scan = setBatchSize(scan, parameters);
            baseReader.read(scan, parameters, transformer);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("Missing table name in parameters");
        } catch (TableNotFoundException e) {
            throw new IllegalArgumentException("Table not found during read: " + parameters.getTableName(),e);
        }

        return read(dbItems, parameters);
    }

    private Scanner addColFamsAndQuals(Scanner scan, AccumuloReadParameters parameters) {
        try {
            String[] colfamsAndQuals = parameters.getColFamsAndQuals();
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
        } catch (NoSuchElementException e){
            if(log.isInfoEnabled())
                log.info("no column family/qualifier were specified");
        }
        return scan;
    }

    private Scanner setBatchSize(Scanner scan, AccumuloReadParameters parameters) {
        try {
            int batchSize = parameters.getBatchSize();
            scan.setBatchSize(batchSize);
        } catch (NoSuchElementException e){
            if(log.isInfoEnabled())
                 log.info(" no property found using default batch size.");
        }
        return scan;
    }


    private Scanner addRange(Scanner scan, AccumuloReadParameters parameters) {
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


    protected Connector getConnectorFromParameters(AccumuloReadParameters parameters) {
        Connector connector;
        try {
            String user = parameters.getUser();
            String pass = parameters.getPassword();
            connector = instance.getConnector(user, pass);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing user/pass");
        } catch (AccumuloSecurityException e) {
            throw new RuntimeException("Security error trying to establish scan connector",e);
        } catch (AccumuloException e) {
            throw new RuntimeException("General Accumulo error while setting up scan connector",e);
        }
        return connector;
    }
}
