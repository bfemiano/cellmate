package cellmate.reader;

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:16 AM
 */
@Beta
public class TupleTransformerDBResultReader<D,C> implements DBResultReader<D,C>{


    public ImmutableList<Tuple<C>> read(Iterable<D> dbItems, ReadParameters parameters) {
        throw new UnsupportedOperationException("only transforming operations allowed by this implementation.");
    }

    public ImmutableList<Tuple<C>> read(Iterable<D> items, ReadParameters parameters, CellTransformer<D, C> transformer) {
        ImmutableList.Builder<Tuple<C>> list = ImmutableList.builder();
        Tuple<C> result;
        Tuple<C> previous = null;
        String current_label = null;
        int maxResults = getMaxResults(parameters);

        int count = 0;
        for(D dbRecord : items){
            if(count >= maxResults)
                break;
            result = transformer.apply(dbRecord, previous);
            if(result != null) {
                if(current_label != null && !current_label.equals(result.getTag())){
                    list.add(previous);
                }
                previous = result;
                current_label = previous.getTag();
            }
            count++;
        }
        if(previous != null)
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
