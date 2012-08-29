package cellmate.writer;

import cellmate.reader.ColFamTransformer;
import cellmate.tuple.TupleBag;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:04 AM
 */
public interface KeyValueProducer<C, V> {

    public V produce(TupleBag<C> bag)
        throws RuntimeException;

    public V produce(TupleBag<C> bag, ImmutableList<ColFamTransformer<C>> transformers)
        throws RuntimeException;
}
