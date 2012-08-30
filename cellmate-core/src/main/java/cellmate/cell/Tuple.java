package cellmate.cell;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/25/12
 * Time: 1:31 PM
 */
@Beta
public final class Tuple<C> {

    private ImmutableList.Builder<C> pairs;

    private String tag;

    private Tuple(){};

    public Tuple(String tag) {
        this.tag = tag;
        pairs = ImmutableList.builder();
    }

    public void addCellTuple(C pair) {
        pairs.add(pair);
    }

    public ImmutableList<C> getInternalList() {
       return pairs.build();
    }

    public String getTag() {
        return tag;
    }
}
