package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Takes cell groups and produces a list of database write objects.
 *
 * This writer implementation simply adds all the database items that
 * return from the transformer to a list. Once every cell group to be written
 * has been processed, return the list of database objects.
 *
 *
 * @param <D> database write objects.
 */
@Beta
public class BasicCelltoRecordWriter<D> implements DBRecordWriter<D> {

    /**
     *
     *
     * @param groups cells to persist
     * @param parameters query options
     * @param transformer function to produce DB write objects from cells.
     * @return ImmutableList of DB write objects.
     * @param <C> cell class type
     * @throws CellExtractorException error occured reading cell contents.
     */
    public <C> ImmutableList<D> write(Iterable<CellGroup<C>> groups, Parameters parameters, DBItemTransformer<D,C> transformer)
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

    public <C> ImmutableList<D> write(Parameters parameters, DBItemTransformer<D,C> transformer) {
        throw new UnsupportedOperationException("this implementation requires an iterable");
    }
}