package cellmate.accumulo.reader;

import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.reader.AggregateCellGroupingDBResultReader;
import cellmate.reader.BasicCellGroupingDBResultReader;
import cellmate.reader.CellTransformer;
import cellmate.reader.DBResultReader;
import com.google.common.annotations.VisibleForTesting;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.util.List;
import java.util.Map;

/**
 * Accumulo specific DB implementation that delegates to the core
 * Accumulo DB result reader with an injected instance of
 * AggregateCellGroupingDBResultReader to handle cell aggregations over Accumulo
 * records.
 *
 */
public class AccumuloAggregateDBResultReader
        implements DBResultReader<Map.Entry<Key,Value>> {

    private AccumuloDBResultReader coreResultReader;

    @VisibleForTesting
    public AccumuloAggregateDBResultReader(Instance instance){
        coreResultReader = new AccumuloDBResultReader(
                new AggregateCellGroupingDBResultReader<Map.Entry<Key, Value>>(),
                instance);
    }

    public AccumuloAggregateDBResultReader(AccumuloParameters parameters){
        coreResultReader = new AccumuloDBResultReader(
                new AggregateCellGroupingDBResultReader<Map.Entry<Key, Value>>(),
                parameters);
    }



    public AccumuloAggregateDBResultReader(String instanceName, String zookeepers){
        coreResultReader = new AccumuloDBResultReader(
                new AggregateCellGroupingDBResultReader<Map.Entry<Key, Value>>(),
                instanceName,
                zookeepers);
    }


    /**
     * {@link AccumuloDBResultReader #read()}
     *
     * @param dbItems
     * @param parameters
     * @param transformer
     * @return List of cells
     */
    public <C> List<CellGroup<C>> read(Iterable<Map.Entry<Key, Value>> dbItems,
                                   Parameters parameters, CellTransformer<Map.Entry<Key, Value>, C> transformer) {
         return coreResultReader.read(dbItems, parameters, transformer);
    }

    /**
     * {@link AccumuloDBResultReader #read()}
     *
     * @param parameters
     * @param transformer
     * @return List of cells
     */
    public <C> List<CellGroup<C>> read(Parameters parameters, CellTransformer<Map.Entry<Key, Value>, C> transformer) {
        return coreResultReader.read(parameters, transformer);
    }
}
