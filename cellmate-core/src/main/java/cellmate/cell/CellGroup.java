package cellmate.cell;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

/**
 * Container class that wraps an Immutable list builder and allows
 * tagging the list with some identifying information. </C>
 *
 * @param <C> cell type found in the group.
 */
@Beta
public final class CellGroup<C> {

    private ImmutableList.Builder<C> cells;

    private String tag;

    /**
     * returns an empty cell group with a blank tag.
     *
     * @param <C> cell type.
     * @return CellGroup
     */
    public static <C> CellGroup<C> emptyGroup() {
        return new CellGroup<C>("");
    }

    /**
     *  Constructor.
     *
     * @param tag label for this cell group. Does not need to be distinct across other cell groups.
     */
    public CellGroup(String tag) {
        this.tag = tag;
        cells = ImmutableList.builder();
    }

    /**
     *
     * @param cell to add to the internal list.
     */
    public void addCell(C cell) {
        cells.add(cell);
    }

    /**
     *
     * @return ImmutableList of cells contained in this group.
     */
    public ImmutableList<C> getInternalList() {
       return cells.build();
    }

    /**
     *
     *
     * @return the tag associated with this cell group.
     */
    public String getTag() {
        return tag;
    }
}
