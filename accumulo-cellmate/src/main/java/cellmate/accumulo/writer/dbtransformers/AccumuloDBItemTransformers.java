package cellmate.accumulo.writer.dbtransformers;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import cellmate.writer.DBItemTransformer;
import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * Static helper methods that build DBItemTransformers to generate Mutations from Cell groups.
 *
 * At this time this is untested.
 *
 */
@Beta
public class AccumuloDBItemTransformers {

    /**
     *  Writes the cell label and value as qualifier and value respectively. Requires
     *  {@link cellmate.cell.ColumnFamily} annotation be present
     *
     * @param <C> cell class type.
     * @return DBItemTransformer
     * @throws CellExtractorException if error occurs reading cell value
     */
    public static <C> DBItemTransformer<Mutation, C> primativeCellValueToBytes() {
        return new DBItemTransformer<Mutation, C>() {
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
                String rowId = group.getTag();
                Mutation m1 = new Mutation(rowId);
                List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String label = CellReflector.getLabelAsString(cell);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    m1.put(new Text(colFam), new Text(label), new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }

    /**
     * Same as {@link #primativeCellValueToBytes()}
     *
     * @param colVisAuxName named auxiliary field representing column visibility.
     * @param <C> cell class type
     * @return DBItemTransformer
     * @throws CellExtractorException if error occurs reading cell value
     */
    public static <C> DBItemTransformer<Mutation, C> primativeCellValueToBytesWithColVis
            (final String colVisAuxName) {
        return new DBItemTransformer<Mutation, C>() {
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
                String rowId = group.getTag();
                Mutation m1 = new Mutation(rowId);
                List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String label = CellReflector.getLabelAsString(cell);
                    String colVis = CellReflector.getAuxiliaryValue(String.class, cell, colVisAuxName);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    m1.put(new Text(colFam), new Text(label), new ColumnVisibility(colVis), new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }

    /**
     * Same as {@link #primativeCellValueToBytes()}
     *
     * @param colVisAuxName named auxiliary field representing column visibility.
     * @param timestampAuxName named auxiliary field representing timestamp.
     * @param <C> cell class type
     * @return DBItemTransformer
     * @throws CellExtractorException if error occurs reading cell value
     */
    public static <C> DBItemTransformer<Mutation, C> primativeCellValueToBytesWithColVisAndTimestamp
            (final String timestampAuxName, final String colVisAuxName) {
        return new DBItemTransformer<Mutation, C>() {
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
                String rowId = group.getTag();
                Mutation m1 = new Mutation(rowId);
                List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String label = CellReflector.getLabelAsString(cell);
                    String colVis = CellReflector.getAuxiliaryValue(String.class, cell, colVisAuxName);
                    long ts = CellReflector.getAuxiliaryValue(long.class, cell, timestampAuxName);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    m1.put(new Text(colFam), new Text(label), new ColumnVisibility(colVis), ts, new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }

    /**
     * Same as {@link #primativeCellValueToBytes()}
     *
     * @param timestampAuxName named auxiliary field representing timestamp.
     * @param <C> cell class type
     * @return DBItemTransformer
     * @throws CellExtractorException if error occurs reading cell value
     */
    public static <C> DBItemTransformer<Mutation, C> primativeCellValueToBytesWithTimestamp
            (final String timestampAuxName) {
        return new DBItemTransformer<Mutation, C>() {
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
                String rowId = group.getTag();
                Mutation m1 = new Mutation(rowId);
                List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String label = CellReflector.getLabelAsString(cell);
                    long ts = CellReflector.getAuxiliaryValue(long.class, cell, timestampAuxName);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    m1.put(new Text(colFam), new Text(label),ts, new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }

    /**
     *
     * Takes a map of column family keys and for any cells with matching
     * column families, lookup the desired qualifier for the key. If column family
     * does not match, write label as qualifier.
     *
     * @param colFamsToWriteValueAsQual map of column families and desired label for each.
     * @param <C> cell class type
     * @return DBItemTransformer
     * @throws CellExtractorException if error occurs reading cell value
     */
    public static <C> DBItemTransformer<Mutation, C> mapStringValueToQualWhenColFamMatched
            (final Set<String> colFamsToWriteValueAsQual) {
        return new DBItemTransformer<Mutation, C>() {
            private final byte[] emptyArray = new byte[]{};
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
                String rowId = group.getTag();
                Mutation m1 = new Mutation(rowId);
                List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String qual = CellReflector.getLabelAsString(cell);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    if(colFamsToWriteValueAsQual.contains(colFam)){
                        qual = CellReflector.getValueAsString(cell);
                        value = emptyArray;
                    }
                    m1.put(new Text(colFam), new Text(qual),new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }

    /**
     *
     * Writes all cells as mutations over a single supplied rowId, ignoring the group tag.
     *
     * @param rowId common rowId to write all cells back as mutations.
     * @param <C> cell class type
     * @return DBItemTransformer
     */
    public static <C> DBItemTransformer<Mutation, C> allCellGroupsToSingleRowId
            (final String rowId) {
        return new DBItemTransformer<Mutation, C>() {
            public List<Mutation> apply(CellGroup<C> group) throws CellExtractorException {
               Mutation m1 = new Mutation(rowId);
               List<Mutation> mutations = Lists.newArrayList();
                for (C cell : group.getInternalList()) {
                    String colFam = CellReflector.getColFam(cell);
                    String qual = CellReflector.getLabelAsString(cell);
                    byte[] value = CellReflector.getValueBytesIfPrimative(cell);
                    m1.put(new Text(colFam), new Text(qual),new Value(value));
                }
                mutations.add(m1);
                return mutations;
            }
        };
    }
}
