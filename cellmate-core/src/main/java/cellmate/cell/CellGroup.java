package cellmate.cell;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * User: bfemiano
 * Date: 8/25/12
 * Time: 1:31 PM
 */
@Beta
public final class CellGroup<C> {

    private ImmutableList.Builder<C> cells;

    private String tag;

    public static <C> CellGroup<C> emptyGroup() {
        return new CellGroup<C>("");
    }

    public CellGroup(String tag) {
        this.tag = tag;
        cells = ImmutableList.builder();
    }

    public void addCell(C cell) {
        cells.add(cell);
    }

    public ImmutableList<C> getInternalList() {
       return cells.build();
    }

    public String getTag() {
        return tag;
    }
}
