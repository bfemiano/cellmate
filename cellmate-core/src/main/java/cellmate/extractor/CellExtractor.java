package cellmate.extractor;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.List;

/**
 * basic template for custom extractors.
 *
 */
@Beta
public interface CellExtractor {


    public <C> Collection<C> filter(List<C> cells,
                                    Predicate<? super C> predicate)
                                                    throws CellExtractorException;
}
