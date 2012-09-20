package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.CommonParameters;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import java.util.NoSuchElementException;

/**
 * Reads through scan results and applies cell transformer.</br>
 *
 * This class handles applying the transformer and adding CellGroups to the immutable return list.</br>
 *
 * As the read() method notices changing cell group tags, it will add the current group to the list.
 * The behavior of this class plays a role in your transformer design, as it does not guarantee
 * uniqueness across the result set, but does provide a way to attach some meaningful definition to that particular group.
 *
 * Users can expect the same cell group instance to be passed to their transform unless they assign
 * it a new reference.  A
 * transformer that determines the need for a new cell group can instantiate a new group and return that reference.
 * If the tag is different, this reader class will add that cellgroup to the result set.
 *
 *
 * @param <D> database item from scan
 */
@Beta
public class BasicCellGroupingDBResultReader<D> implements DBResultReader<D>{

    private static final String UNSUPPORTED_OP = "Basic cell reader needs to be sent an iterable and transformer";

    public <C> ImmutableList<CellGroup<C>> read(Parameters parameters, CellTransformer<D, C> transformer) {
        throw new UnsupportedOperationException(UNSUPPORTED_OP);
    }

    /**
     *  Read through database items and apply the transformer to produce cells
     *  and new cell groups. If the tag for a given cell group changes from the
     *  last one, add it to the list.
     *
     *  The rules for cell group generation are left entirely to the transformer.
     *
     * @param items database items
     * @param parameters scan parameters
     * @param transformer cell transformer to produce cells.
     * @param <C> cell type
     * @return  list of cell groups generated by transformer.
     */
    public <C> ImmutableList<CellGroup<C>> read(Iterable<D> items,
                                            Parameters parameters,
                                            CellTransformer<D, C> transformer) {
        ImmutableList.Builder<CellGroup<C>> list = ImmutableList.builder();
        CellGroup<C> EMPTY_GROUP = CellGroup.emptyGroup();
        CellGroup<C> result = EMPTY_GROUP;
        CellGroup<C> previous = EMPTY_GROUP;
        int maxResults = getMaxResults(parameters);

        int count = 0;
        for(D dbRecord : items){
            if(count >= maxResults)
                break;
            try {
                result = transformer.apply(dbRecord, previous);
            } catch (CellExtractorException e){
                throw new RuntimeException("Error during cell transformation from db item " + e.getType().name(),e);
            }
            if(result == null)
                throw new RuntimeException("Supplied cell transformer returned a null cell group reference");
            if(previous != EMPTY_GROUP
                    && !previous.getTag().equals(result.getTag())){ //only add on new non-empty tagged group.
                list.add(previous);
            }
            previous = result;
            count++;
        }
        //add the last seen only if it contains records.
        if(count > 0 &&  previous.getInternalList().size() > 0)
            list.add(previous);
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
