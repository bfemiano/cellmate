package cellmate.reader;

import cellmate.cell.CellGroup;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:16 AM
 */
@Beta
public class BasicCellGroupingDBResultReader<D,C> implements DBResultReader<D,C>{

    private CellGroup<C> EMPTY_INITIAL_GROUP = CellGroup.emptyGroup();
    private static final String UNSUPPORTED_OP = "Basic cell reader needs to be sent an iterable and transformer";

    public ImmutableList<CellGroup<C>> read(ReadParameters parameters, CellTransformer<D, C> transformer) {
        throw new UnsupportedOperationException(UNSUPPORTED_OP);
    }

    public ImmutableList<CellGroup<C>> read(Iterable<D> items,
                                            ReadParameters parameters,
                                            CellTransformer<D, C> transformer) {
        ImmutableList.Builder<CellGroup<C>> list = ImmutableList.builder();
        CellGroup<C> result = EMPTY_INITIAL_GROUP;
        CellGroup<C> previous = EMPTY_INITIAL_GROUP;
        int maxResults = getMaxResults(parameters);

        int count = 0;
        for(D dbRecord : items){
            if(count >= maxResults)
                break;
            result = transformer.apply(dbRecord, previous);
            if(result == null)
                throw new RuntimeException("Supplied cell transformer returned a null cell group reference");
            if(previous != EMPTY_INITIAL_GROUP
                    && !previous.getTag().equals(result.getTag())){ //only add on new non-empty tagged group.
                list.add(previous);
            }
            previous = result;
            count++;
        }
        if(count > 0 && previous != EMPTY_INITIAL_GROUP) //found at least one
            list.add(previous);
        return list.build();
    }



    private int getMaxResults(ReadParameters parameters) {
        try {
            return parameters.getInt(CommonReadParameters.MAX_RESULTS);
        } catch (NoSuchElementException e){
            return Integer.MAX_VALUE;
        }
    }
}
