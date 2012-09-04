package cellmate.reader;


import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 12:14 AM
 */
@Beta
public abstract class DBResultToTupleReader<D,C> implements DBResultReader<D, C> {

    public ImmutableList<Tuple<C>> read(Iterable<D> items, int maxResultsPerQuery) {
        ImmutableList.Builder<Tuple<C>> list = ImmutableList.builder();
        Tuple<C> result = null;
        Tuple<C> previous = null;
        String current_label = null;

        int count = 0;
        for(D dbRecord : items){
            if(count >= maxResultsPerQuery)
                break;
            result = addCells(dbRecord, previous);
            if(current_label != null && !current_label.equals(result.getTag())){
                list.add(previous);
            }
            previous = result;
            current_label = previous.getTag();
            count++;
        }
        if(previous != null)
            list.add(previous);
        return list.build();
    }

    public ImmutableList<Tuple<C>> read(Iterable<D> items) {
        return read(items, Integer.MAX_VALUE);
    }
}
