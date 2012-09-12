package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.CommonParameters;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:16 AM
 */
@Beta
public class AggregateCellGroupingDBResultReader<D,C> implements DBResultReader<D,C>{

    private CellGroup<C> EMPTY_INITIAL_GROUP = CellGroup.emptyGroup();
    private static final String UNSUPPORTED_OP = "Aggregate cell reader needs to be sent an iterable and transformer";

    public ImmutableList<CellGroup<C>> read(Parameters parameters, CellTransformer<D, C> transformer) {
        throw new UnsupportedOperationException(UNSUPPORTED_OP);
    }

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

