package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.CommonParameters;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.NoSuchElementException;

/**
 * Iterates over database results and applies the transformer to
 * produce cells.
 *
 *
 * @param <D> database from a scan
 * @param <C> cell class.
 */
@Beta
public class AggregateCellGroupingDBResultReader<D,C> implements DBResultReader<D,C>{

    private CellGroup<C> EMPTY_INITIAL_GROUP = CellGroup.emptyGroup();
    private static final String UNSUPPORTED_OP = "Aggregate cell reader needs to be sent an iterable and transformer";

    public ImmutableList<CellGroup<C>> read(Parameters parameters, CellTransformer<D, C> transformer) {
        throw new UnsupportedOperationException(UNSUPPORTED_OP);
    }

    /**
     *    Reads through database records that come back from scan, usually Key/Value pairs.
     *    For each item, apply a cell transformer.
     *
     *    After the final iteration, one more application of the transformer is
     *    passed with null. This serves as a flag that cell transformers can use to
     *    return aggregate summaries of everything that occured during iteration.
     *
     *    If there was DB item iteration, add the final result to the list of cell groups
     *    to return. Otherwise return an empty list.
     *
     * @param items database items produce during scan
     * @param parameters scan parameters
     * @param transformer  cell transformer to produce cells from db items
     * @return ImmutableList
     */
    public ImmutableList<CellGroup<C>> read(Iterable<D> items,
                                            Parameters parameters,
                                            CellTransformer<D, C> transformer) {
        ImmutableList.Builder<CellGroup<C>> list = ImmutableList.builder();
        CellGroup<C> result = EMPTY_INITIAL_GROUP;
        CellGroup<C> previous = EMPTY_INITIAL_GROUP;
        int maxResults = getMaxResults(parameters);

        int count = 0;
        for(D dbRecord : items){
            if(count >= maxResults)
                break;
            try {
                result = transformer.apply(dbRecord, previous);
            } catch (CellExtractorException e){
                throw new RuntimeException("Error during cell transformation from db item " + e.getType().name(), e);
            }
            if(result == null)
                throw new RuntimeException("Supplied cell transformer returned a null cell group reference");
            previous = result;
            count++;
        }
        if(count > 0){
            try {
                result = transformer.apply(null, previous); //null input signals end of iteration, and we can write the aggregate values.
            } catch (CellExtractorException e){
                throw new RuntimeException("Error writing the aggregate cell values", e);
            }
            list.add(result);
        }
        return list.build();
    }



    private int getMaxResults(Parameters parameters) {
        try {
            return parameters.getInt(CommonParameters.MAX_RESULTS);
        } catch (NoSuchElementException e){
            return Integer.MAX_VALUE;
        }
    }
}

