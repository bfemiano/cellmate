package cellmate.accumulo.writer;

import cellmate.accumulo.parameters.AccumuloParameterOps;
import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.ErrorType;
import cellmate.reader.BasicCellGroupingDBResultReader;
import cellmate.reader.DBResultReader;
import cellmate.writer.BasicCelltoRecordWriter;
import cellmate.writer.DBItemTransformer;
import cellmate.writer.DBRecordWriter;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

/**
 * User: bfemiano
 * Date: 9/12/12
 * Time: 2:00 PM
 */
public class AccumuloDBRecordWriter<C>
        implements DBRecordWriter<Mutation, C>{

    private Instance instance;
    private String instanceName;
    private String zookeepers;
    private static final Logger log = Logger.getLogger(AccumuloDBRecordWriter.class);
    private static final Pattern colon = Pattern.compile("[:]");
    private DBRecordWriter<Mutation, C> baseWriter;


    @VisibleForTesting
    public AccumuloDBRecordWriter(Instance instance){
        this.instance = instance;
        baseWriter = new BasicCelltoRecordWriter<Mutation, C>();
    }

    @VisibleForTesting
    public AccumuloDBRecordWriter(DBRecordWriter<Mutation, C> baseWriter, Instance instance){
        this.instance = instance;
        this.baseWriter = baseWriter;
    }

    public AccumuloDBRecordWriter(AccumuloParameters parameters){
        try {
            String instanceName = parameters.getInstanceName();
            String zookeepers = parameters.getZookeepers();
            this.instance = new ZooKeeperInstance(instanceName, zookeepers);
            this.baseWriter = new BasicCelltoRecordWriter<Mutation, C>();
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing zookeepers and/or instance id");
        }
    }

    public AccumuloDBRecordWriter(DBRecordWriter<Mutation, C> baseWriter,
                                  AccumuloParameters parameters) {
        this(parameters);
        this.baseWriter = baseWriter;
    }

    public AccumuloDBRecordWriter(DBRecordWriter<Mutation, C> baseWriter,
                                  String instanceName,
                                  String zookeepers){
        this.baseWriter = baseWriter;
        instance = new ZooKeeperInstance(instanceName, zookeepers);
    }

    public AccumuloDBRecordWriter(String instanceName, String zookeepers){
        baseWriter = new BasicCelltoRecordWriter<Mutation, C>();
        instance = new ZooKeeperInstance(instanceName, zookeepers);
    }

    public ImmutableList<Mutation> write(Iterable<CellGroup<C>> groups,
                                         Parameters params,
                                         DBItemTransformer<Mutation,C> transformer) throws CellExtractorException {
        AccumuloParameters parameters = AccumuloParameterOps.checkParamType(params);
        Connector connector = AccumuloParameterOps.getConnectorFromParameters(instance, parameters);
        try {
            BatchWriter writer = connector.createBatchWriter(parameters.getTableName(),
                    parameters.getMaxWriteMemory(),
                    parameters.getMaxWriteLatency(),
                    parameters.getMaxWriteThreads());
            ImmutableList<Mutation> items = baseWriter.write(groups, parameters, transformer);
            writer.addMutations(items);
            writer.close();
            return items;
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("Missing table name in parameters");
        } catch (TableNotFoundException e) {
            throw new IllegalArgumentException("Table not found during read: " + parameters.getTableName(),e);
        } catch (MutationsRejectedException e) {
            throw new CellExtractorException("Accumulo mutations were rejected", e, ErrorType.REJECTED_WRITE);
        }
    }

    public ImmutableList<Mutation> write(Parameters params,
                                         DBItemTransformer<Mutation,C> transformer) throws CellExtractorException {
        return baseWriter.write(params, transformer);
    }
}
