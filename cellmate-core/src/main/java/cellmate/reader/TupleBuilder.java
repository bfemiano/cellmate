package cellmate.reader;


import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;
import org.apache.commons.collections.list.PredicatedList;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 2:28 PM
 */
public interface TupleBuilder<I, C> {

    public TupleBag<C> buildTupleBag(I record)
            throws IllegalArgumentException;

    public boolean hasBagLabel(I record, String label)
            throws IllegalArgumentException;

    public C getTuple(I record);

    public C getTuple(I record, ImmutableList<ColFamTransformer<I>> transformers);
}
