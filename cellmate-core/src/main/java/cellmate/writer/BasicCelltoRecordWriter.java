package cellmate.writer;

import cellmate.cell.CellGroup;
import cellmate.cell.parameters.Parameters;
import cellmate.extractor.CellExtractorException;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 12:18 PM
 */
@Beta
public class BasicCelltoRecordWriter<D,C> implements DBRecordWriter<D,C> {


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