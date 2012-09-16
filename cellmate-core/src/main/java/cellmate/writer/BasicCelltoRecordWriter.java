package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Takes cell groups of class represented by the parameterized C
 * and produces DB write objects.
 *
 * @param <D> database write objects
 * @param <C> cell
 */
@Beta
public class BasicCelltoRecordWriter<D,C> implements DBRecordWriter<D,C> {

    /**
     *
     *
     * @param groups cells to persist
     * @param parameters query options
     * @param transformer function to produce DB write objects from cells.
     * @return ImmutableList of DB write objects.
     * @throws CellExtractorException error occured reading cell contents.
     */
    public ImmutableList<D> write(Iterable<CellGroup<C>> groups, Parameters parameters, DBItemTransformer<D,C> transformer)
        throws CellExtractorException{
        ImmutableList.Builder<D> list = ImmutableList.builder();
        List<D> dbItems = Lists.newArrayList();
        for(CellGroup<C> cellGroup : groups){
            dbItems = transformer.apply(cellGroup);
            if(dbItems != null && dbItems.size() > 0)
                list.addAll(dbItems);
        }
        return list.build();
    }

    public ImmutableList<D> write(Parameters parameters, DBItemTransformer<D,C> transformer) {
        throw new UnsupportedOperationException("this implementation requires an iterable");
    }
}