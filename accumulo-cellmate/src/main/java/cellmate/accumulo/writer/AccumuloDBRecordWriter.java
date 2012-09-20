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
import com.google.common.annotations.Beta;
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
 *
 * Handles reading query parameters, applying DBTransformer operations to create
 * Mutations from different cell groups, and writing those Mutations to Accumulo.
 */
@Beta
public class AccumuloDBRecordWriter implements DBRecordWriter<Mutation>{

    private Instance instance;
    private String instanceName;
    private String zookeepers;
    private static final Logger log = Logger.getLogger(AccumuloDBRecordWriter.class);
    private static final Pattern colon = Pattern.compile("[:]");
    private DBRecordWriter<Mutation> baseWriter;


    @VisibleForTesting
    public AccumuloDBRecordWriter(Instance instance){
        this.instance = instance;
        baseWriter = new BasicCelltoRecordWriter<Mutation>();
    }

    @VisibleForTesting
    public AccumuloDBRecordWriter(DBRecordWriter<Mutation> baseWriter, Instance instance){
        this.instance = instance;
        this.baseWriter = baseWriter;
    }

    /**
     * Constructor
     *
     * Gets an Accumulo Instance and creates a new {@link BasicCelltoRecordWriter}
     *
     * @param parameters expected to have instance name and comma-delimited zookeeper list.
     */
    public AccumuloDBRecordWriter(AccumuloParameters parameters){
        try {
            String instanceName = parameters.getInstanceName();
            String zookeepers = parameters.getZookeepers();
            this.instance = new ZooKeeperInstance(instanceName, zookeepers);
            this.baseWriter = new BasicCelltoRecordWriter<Mutation>();
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing zookeepers and/or instance id");
        }
    }

    /**
     * Constructor
     *
     * Allows custom DBRecordWriter to be used in place of the default {@link BasicCelltoRecordWriter}
     *
     * @param baseWriter injected writer to delegate operations.
     * @param parameters expected to have instance name and comma-delimted zookeeper list.
     */
    public AccumuloDBRecordWriter(DBRecordWriter<Mutation> baseWriter,
                                  AccumuloParameters parameters) {
        this(parameters);
        this.baseWriter = baseWriter;
    }

    /**
     * Constructor
     *
     * Allows custom DBRecordWriter to be used in place of the default {@link BasicCelltoRecordWriter}.
     *
     * @param baseWriter injected writer to delegate operations.
     * @param instanceName Accumulo instance name
     * @param zookeepers comma-delimited zookeeper list
     */
    public AccumuloDBRecordWriter(DBRecordWriter<Mutation> baseWriter,
                                  String instanceName,
                                  String zookeepers){
        this.baseWriter = baseWriter;
        instance = new ZooKeeperInstance(instanceName, zookeepers);
    }

    public AccumuloDBRecordWriter(String instanceName, String zookeepers){
        baseWriter = new BasicCelltoRecordWriter<Mutation>();
        instance = new ZooKeeperInstance(instanceName, zookeepers);
    }

    /**
     *  Iterate over the different cell groups and build mutations for persistence back to Accumulo.
     *
     * @param groups to generate mutations.
     * @param params to build write operation.
     * @param transformer to generate mutations from the cell groups.
     * @param <C> cell type
     * @return ImmutableList of mutations.
     * @throws CellExtractorException if an error occurs in the DBItemTransformer
     */
    public <C> ImmutableList<Mutation> write(Iterable<CellGroup<C>> groups,
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

    /**
     *
     * Delegates to the base writer write()
     *
     * @param params to build write operations.
     * @param transformer to generate mutations from cell groups.
     * @param <C> cell type
     * @return ImmutableList of mutations.
     * @throws CellExtractorException if an error occurs in the DBItemTransformer
     */
    public <C> ImmutableList<Mutation> write(Parameters params,
                                         DBItemTransformer<Mutation,C> transformer) throws CellExtractorException {
        return baseWriter.write(params, transformer);
    }
}
