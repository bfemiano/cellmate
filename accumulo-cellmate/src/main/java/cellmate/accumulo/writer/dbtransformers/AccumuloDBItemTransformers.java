package cellmate.accumulo.writer.dbtransformers;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import cellmate.writer.DBItemTransformer;
import com.google.common.collect.Lists;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.ColumnVisibility;
import org.apache.hadoop.io.Text;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User: bfemiano
 * Date: 9/12/12
 * Time: 2:45 PM
 */
public class AccumuloDBItemTransformers {

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
