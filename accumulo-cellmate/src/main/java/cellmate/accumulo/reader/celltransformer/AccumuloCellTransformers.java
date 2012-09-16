package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.*;
import cellmate.cell.CellGroup;
import cellmate.cell.DoubleValueCell;
import cellmate.cell.IntValueCell;
import cellmate.extractor.CellExtractorException;
import cellmate.reader.CellTransformer;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Static helper methods that offer concrete Cell transformers that cover a variety of
 * common use cases for reading Accumulo Key/Value pairs into Cell groups.</br></br>
 *
 * The type-specific ValueQualToLabel methods set the cell label to the db qualifer, and the
 * cell value to the db item value. Depending on which static method you call, you can optionally
 * add ColumnFamily, and/or ColumnVisibility/Timestamp.  The different typed versions are useful
 * for automatically reading byte[] values directly to other primative types, but can produce undesirable
 * behavior if the raw byte[] contents in the DB value were not persisted as the desired type.</br></br>
 *
 * For instance, a String written as a Key/Value pair to a byte[] might not appear properly when read
 * back into a double[], even if the actual String could be cast as a double.</br></br>
 *
 * For this reason, most people will be interested in the String and byte[] versions of the ValueQualToLabel() transformer
 * series. See {@link cellmate.accumulo.reader.celltransformer.SecurityByteCellTransformer} and
 * {@link cellmate.accumulo.reader.celltransformer.SecurityStringCellTransformer}</br></br>
 *
 * A few additional transformer implementations are provided that collect all the records in a common bag, apply artifical
 * labels on demand, and offer qualifier and total row aggregations.
 *
 */
public class AccumuloCellTransformers {

    /**
     * Get an instance of SecurityStringCellTransformer that reads qualifer to label, value to String value, and
     * column family, column visibility, and timestamp.
     *
     * The slightly different method names offer convinent ways to grab different combinations of the above settings, including
     * different value types and exlusion/inclusion of column family, column visibility, and timestamp.
     *
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityStringCellTransformer(true, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabelWithTime_ColVis() {
        return new SecurityStringCellTransformer(true, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabelWithColFam() {
        return new SecurityStringCellTransformer(false, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabel() {
        return new SecurityStringCellTransformer(false, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityIntCellTransformer(true, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabelWithTime_ColVis() {
        return new SecurityIntCellTransformer(true, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabelWithColFam() {
        return new SecurityIntCellTransformer(false, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabel() {
        return new SecurityIntCellTransformer(false, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityLongCellTransformer(true, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabelWithTime_ColVis() {
        return new SecurityLongCellTransformer(true, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabelWithColFam() {
        return new SecurityLongCellTransformer(false, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabel() {
        return new SecurityLongCellTransformer(false, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityDoubleCellTransformer(true, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabelWithTime_ColVis() {
        return new SecurityDoubleCellTransformer(true, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabelWithColFam() {
        return new SecurityDoubleCellTransformer(false, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabel() {
        return new SecurityDoubleCellTransformer(false, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityByteCellTransformer(true, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabelWithTime_ColVis() {
        return new SecurityByteCellTransformer(true, false);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabelWithColFam() {
        return new SecurityByteCellTransformer(false, true);
    }

    /**
     *
     * {@link #stringValueQualToLabelWithTime_ColVis_ColFam()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabel() {
        return new SecurityByteCellTransformer(false, false);
    }

    /**
     * Transformer that takes each incoming DB item and places in the same cell group. The DBReader will return
     * one single Cell Group containing all the qualifiers and values see in the scan, regardless of which
     * row key or column family they came from.
     *
     * Column family, column visibility, and timestamp are not written to the aux fields by this transformer.
     *
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> singleGroupByteValueCells() {
        return new CellTransformer<Map.Entry<Key, Value>, SecurityByteValueCell>() {
            public CellGroup<SecurityByteValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                          CellGroup<SecurityByteValueCell> group)
                    throws CellExtractorException {
                String label = dbItem.getKey().getColumnQualifier().toString();
                byte[] value = dbItem.getValue().get();
                SecurityByteValueCell cell = new SecurityByteValueCell(label, value);
                group.addCell(cell);
                return group;
            }
        };
    }

    /**
     *
     *
     * {@link #singleGroupByteValueCells()}
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> singleGroupByteValueCellsWithTime_ColVis_ColFam() {
        return new CellTransformer<Map.Entry<Key, Value>, SecurityByteValueCell>() {
            public CellGroup<SecurityByteValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                          CellGroup<SecurityByteValueCell> group)
                    throws CellExtractorException {
                String label = dbItem.getKey().getColumnQualifier().toString();
                byte[] value = dbItem.getValue().get();
                String colVis = dbItem.getKey().getColumnVisibility().toString();
                String colFam = dbItem.getKey().getColumnFamily().toString();
                long timestamp = dbItem.getKey().getTimestamp();
                SecurityByteValueCell cell = new SecurityByteValueCell(label, value, timestamp, colVis, colFam);
                group.addCell(cell);
                return group;
            }
        };
    }

    /**
     * Transformer that takes a mapping of which column families get a constant label. Useful if your
     * column family contains a group of qualifiers with no values, where the context for those qualifiers
     * is simply their existance in the column family itself.
     *
     * If a Key/Value pair has a ColumnFamily not found in the map, transformer normally by the same logic
     * found in the normal string value transformer. See also {@link #stringValueQualtoLabel()}
     *
     * @param colFamToCommonLabel map of column families to apply common label.
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> colFamToCommonLabelOnMatches(
            final Map<String, String> colFamToCommonLabel) {
        return new CellTransformer<Map.Entry<Key, Value>, SecurityStringValueCell>() {
            public CellGroup<SecurityStringValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                            CellGroup<SecurityStringValueCell> cellGroup) throws CellExtractorException {
                String activeRowId = dbItem.getKey().getRow().toString();
                if (!cellGroup.getTag().equals(activeRowId)) {
                    cellGroup = new CellGroup<SecurityStringValueCell>(activeRowId);
                }
                String colFamStr = dbItem.getKey().getColumnFamily().toString();
                String label = dbItem.getKey().getColumnQualifier().toString();
                String value = new String(dbItem.getValue().get());
                if(colFamToCommonLabel.containsKey(colFamStr)){
                    value = label;
                    label = colFamToCommonLabel.get(colFamStr);
                }
                SecurityStringValueCell cell = new SecurityStringValueCell(label, value, colFamStr);
                cellGroup.addCell(cell);
                return cellGroup;
            }
        };
    }

    /**
     * Aggregate transformer to read the Key/Value and for any matching the supplied qualifier,
     * treat the Value byte[] as an integer. Return the average int value seen for all Key/Value
     * containing the qualifier.
     *
     * null  DBItem flag is sent by the Aggregate reader to signal end of scan iteration.
     *
     * This class works in conjunction with {@link cellmate.reader.AggregateCellGroupingDBResultReader}
     *
     * @param qual to calculate average int value.
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, DoubleValueCell> averageSingleQual (final String qual)
    {
        return new CellTransformer<Map.Entry<Key, Value>, DoubleValueCell>() {
            private int sum;
            private int count;
            public CellGroup<DoubleValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                    CellGroup<DoubleValueCell> group) throws CellExtractorException {
                if(dbItem != null) {
                    if(dbItem.getKey().getColumnQualifier().toString().equals(qual)){
                        byte[] valBytes = dbItem.getValue().get();
                        if(valBytes.length > 0)  {
                            sum += ByteBuffer.wrap(valBytes).asIntBuffer().get();
                            count++;
                        }
                    }
                } else { //null dbItem is final signal and reader to write sum as cell
                    if(count > 0)  {
                        double average = ((double)sum)/count;
                        DoubleValueCell cell = new DoubleValueCell(qual, average);
                        group.addCell(cell);
                    }
                }
                return group;
            }
        };
    }

    /**
     *  If reading Key/Values where the Value byte[] are integers, this transformer lets you
     *  get an aggregate sum of all the integer values that match a given qualifier.
     *
     * null  DBItem flag is sent by the Aggregate reader to signal end of scan iteration.
     *
     * This class works in conjunction with {@link cellmate.reader.AggregateCellGroupingDBResultReader}
     *
     * @param qual to flag a Key/Value pair as an integer value and add to the sum.
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, IntValueCell> aggregateSingleQual(final String qual)
    {
        return new CellTransformer<Map.Entry<Key, Value>, IntValueCell>() {
            private int aggValue;
            public CellGroup<IntValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                 CellGroup<IntValueCell> group) throws CellExtractorException {
                if(dbItem != null) {
                    if(dbItem.getKey().getColumnQualifier().toString().equals(qual)){
                        byte[] valBytes = dbItem.getValue().get();
                        if(valBytes.length > 0)
                            aggValue += ByteBuffer.wrap(valBytes).asIntBuffer().get();
                    }
                } else { //null dbItem is final signal and reader to write aggValue as cell
                    IntValueCell cell = new IntValueCell(qual, aggValue);
                    group.addCell(cell);
                }
                return group;
            }
        };
    }

    /**
     * Same as aggregateSingleQual() but for multiple qualifiers.
     *
     * null DBItem flag is sent by the Aggregate reader to signal end of scan iteration.
     *
     * This class works in conjunction with {@link cellmate.reader.AggregateCellGroupingDBResultReader}
     *
     * @param qualifiers to flag a Key/Value pair as an integer value and add to the sum.
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, IntValueCell> aggregateMultiQual(final String... qualifiers)
    {
        return new CellTransformer<Map.Entry<Key, Value>, IntValueCell>() {
            private Map<String, Integer> qualAggs;
            public CellGroup<IntValueCell> apply(Map.Entry<Key, Value> dbItem,
                                                 CellGroup<IntValueCell> group) throws CellExtractorException {
                if(dbItem != null) {
                    if(qualAggs == null) {
                        qualAggs = new HashMap<String, Integer>();
                        for(String item : qualifiers){
                            qualAggs.put(item, 0);
                        }
                    }
                    for(String qual : qualAggs.keySet()) {
                        if(dbItem.getKey().getColumnQualifier().toString().equals(qual)){
                            byte[] valBytes = dbItem.getValue().get();
                            if(valBytes.length > 0)
                                qualAggs.put(qual, qualAggs.get(qual) + ByteBuffer.wrap(valBytes).asIntBuffer().get());
                        }
                    }
                } else {
                    for(Map.Entry<String,Integer> agg : qualAggs.entrySet()) {
                        IntValueCell cell = new IntValueCell(agg.getKey(), agg.getValue());
                        group.addCell(cell);
                    }
                }
                return group;
            }
        };
    }

    /**
     * Transformer that ignores the DB item contents and simply tallys how many discrete Key/Value
     * pairs were seen by the scan.
     *
     * null DBItem flag is sent by the Aggregate reader to signal end of scan iteration.
     *
     * This class works in conjunction with {@link cellmate.reader.AggregateCellGroupingDBResultReader}
     *
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, IntValueCell> totalKeyValueCount()
    {
        return new CellTransformer<Map.Entry<Key, Value>, IntValueCell>() {
            private int totalCount = 0;
            private static final String LABEL = "kvCount";
            public CellGroup<IntValueCell> apply(Map.Entry<Key, Value> dbItem, CellGroup<IntValueCell> group)
                    throws CellExtractorException {
                if(dbItem != null) {
                    totalCount++;
                } else {
                    IntValueCell cell = new IntValueCell(LABEL, totalCount);
                    group.addCell(cell);
                }
                return group;
            }
        };
    }

    /**
     * Trnasformer that ignores DB item contents and simply tallys how many unique rowIDs
     * were seen by the scan.
     *
     * null DBItem flag is sent by the Aggregate reader to signal end of scan iteration.
     *
     * This class works in conjunction with {@link cellmate.reader.AggregateCellGroupingDBResultReader}
     *
     * @return CellTransformer
     */
    public static CellTransformer<Map.Entry<Key,Value>, IntValueCell> distinctRowIDCount()
    {
        return new CellTransformer<Map.Entry<Key, Value>, IntValueCell>() {
            private int totalCount = 0;
            private static final String LABEL = "rowIdCount";
            private String prevLabel = null;
            public CellGroup<IntValueCell> apply(Map.Entry<Key, Value> dbItem, CellGroup<IntValueCell> group)
                    throws CellExtractorException {
                if(dbItem != null) {
                    if(prevLabel != null && !prevLabel.equals(dbItem.getKey().getRow().toString())) {
                        totalCount++;
                    }
                    prevLabel = dbItem.getKey().getRow().toString();
                } else {
                    IntValueCell cell = new IntValueCell(LABEL, totalCount+1); //first transition will be ignored. offset by one to account for this.
                    group.addCell(cell);
                }
                return group;
            }
        };
    }

}
