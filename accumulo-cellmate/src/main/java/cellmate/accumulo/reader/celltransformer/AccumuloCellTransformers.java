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
 * User: bfemiano
 * Date: 9/11/12
 * Time: 5:25 PM
 */
public class AccumuloCellTransformers {

    //static helper methods for getting instances of useful transform implementations.

    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityStringCellTransformer(true, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabelWithTime_ColVis() {
        return new SecurityStringCellTransformer(true, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabelWithColFam() {
        return new SecurityStringCellTransformer(false, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityStringValueCell> stringValueQualtoLabel() {
        return new SecurityStringCellTransformer(false, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityIntCellTransformer(true, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabelWithTime_ColVis() {
        return new SecurityIntCellTransformer(true, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabelWithColFam() {
        return new SecurityIntCellTransformer(false, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityIntValueCell> intValueQualtoLabel() {
        return new SecurityIntCellTransformer(false, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityLongCellTransformer(true, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabelWithTime_ColVis() {
        return new SecurityLongCellTransformer(true, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabelWithColFam() {
        return new SecurityLongCellTransformer(false, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityLongValueCell> longValueQualtoLabel() {
        return new SecurityLongCellTransformer(false, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityDoubleCellTransformer(true, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabelWithTime_ColVis() {
        return new SecurityDoubleCellTransformer(true, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabelWithColFam() {
        return new SecurityDoubleCellTransformer(false, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityDoubleValueCell> doubleValueQualtoLabel() {
        return new SecurityDoubleCellTransformer(false, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualToLabelWithTime_ColVis_ColFam() {
        return new SecurityByteCellTransformer(true, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabelWithTime_ColVis() {
        return new SecurityByteCellTransformer(true, false);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabelWithColFam() {
        return new SecurityByteCellTransformer(false, true);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> bytesValueQualtoLabel() {
        return new SecurityByteCellTransformer(false, false);
    }

    public static ColFamToCommonLabelMapCellTransformer commonLabelOnColFamMatches(Map<String, String> colFamToLabelMap) {
        return new ColFamToCommonLabelMapCellTransformer(colFamToLabelMap);
    }

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> singleBagByteValueCells() {
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

    public static CellTransformer<Map.Entry<Key,Value>, SecurityByteValueCell> singleBagByteValueCellsWithTime_ColVis_ColFam() {
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
