package cellmate.accumulo.reader.celltransformer;

import cellmate.accumulo.cell.*;
import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.accumulo.reader.AccumuloAggregateDBResultReader;
import cellmate.accumulo.reader.AccumuloDBResultReader;
import cellmate.cell.CellGroup;
import cellmate.cell.DoubleValueCell;
import cellmate.cell.IntValueCell;
import cellmate.extractor.*;
import cellmate.reader.DBResultReader;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * User: bfemiano
 * Date: 9/11/12
 * Time: 1:11 AM
 */
public class CellTransformerTest {

    Instance mockInstance;
    AccumuloParameters parameters;
    AccumuloParameters.Builder builder;
    private static final String PEOPLE_TABLE = "people";
    private static final long MAX_MEMORY= 10000L;
    private static final long MAX_LATENCY=1000L;
    private static final int MAX_WRITE_THREADS = 4;
    private static final Text INFO_CF = new Text("info");
    private static final Text EVENTS_CF = new Text("events");
    private static final Text NAME = new Text("name");
    private static final Text AGE = new Text("age");
    private static final Text SIBLINGS = new Text("siblings");
    private static final Text LIFETIME_MILLS = new Text("lm");
    private static final Text GPA = new Text("gpa");
    private static final Text HEIGHT = new Text("height");
    private static final Text EVENT1 = new Text("2342");
    private static final Text EVENT2 = new Text("2343");

    private SingleMultiValueCellExtractor extractor = new SingleMultiValueCellExtractor();
    private CommonAuxiliaryFieldsCellExtractor auxExtractor = new CommonAuxiliaryFieldsCellExtractor();

    @BeforeClass
    public void setupParamsAndInstance()
            throws AccumuloException, AccumuloSecurityException, TableExistsException, TableNotFoundException {
        mockInstance = new MockInstance("testInstance");
        Connector conn = mockInstance.getConnector("user1", "password".getBytes());
        if(conn.tableOperations().exists(PEOPLE_TABLE))
            conn.tableOperations().delete(PEOPLE_TABLE);
        conn.tableOperations().create(PEOPLE_TABLE);
    }

    @BeforeMethod
    public void setupBuilder() {
        builder = new AccumuloParameters.Builder().
                setUser("user1").
                setZookeepers("localhost:2181").
                setInstanceName("testInstance").
                setPassword("password").
                setMaxResults(100).
                setTable(PEOPLE_TABLE);
    }

    @BeforeClass(dependsOnMethods={"setupParamsAndInstance"})
    public void addData() {
        try {
            Connector conn = mockInstance.getConnector("user1", "password");
            BatchWriter writer = conn.createBatchWriter
                    (PEOPLE_TABLE, MAX_MEMORY, MAX_LATENCY, MAX_WRITE_THREADS);
            Mutation m1 = new Mutation("row1");
            m1.put(INFO_CF, NAME, new Value("brian".getBytes()));
            m1.put(INFO_CF, AGE, new Value(ByteBuffer.wrap(new byte[4]).putInt(30).array()));
            m1.put(INFO_CF, SIBLINGS, new Value(ByteBuffer.wrap(new byte[4]).putInt(3).array()));
            m1.put(INFO_CF, LIFETIME_MILLS, new Value(ByteBuffer.wrap(new byte[8]).putLong(12345l).array()));
            m1.put(INFO_CF, GPA, new Value(ByteBuffer.wrap(new byte[8]).putDouble(3.7d).array()));
            m1.put(INFO_CF, HEIGHT, new Value("6ft".getBytes()));
            m1.put(EVENTS_CF, EVENT1, new Value("".getBytes()));
            m1.put(EVENTS_CF, EVENT2, new Value("".getBytes()));

            Mutation m2 = new Mutation("row2");
            m2.put(INFO_CF, NAME, new Value("adam".getBytes()));
            m2.put(INFO_CF, AGE, new Value(ByteBuffer.wrap(new byte[4]).putInt(29).array()));
            m2.put(INFO_CF, SIBLINGS, new Value(ByteBuffer.wrap(new byte[4]).putInt(6).array()));
            m2.put(INFO_CF, LIFETIME_MILLS, new Value(ByteBuffer.wrap(new byte[8]).putLong(6789l).array()));
            m2.put(INFO_CF, GPA, new Value(ByteBuffer.wrap(new byte[8]).putDouble(3.2d).array()));
            m2.put(INFO_CF, HEIGHT, new Value("5ft7inch".getBytes()));
            m2.put(EVENTS_CF, EVENT1, new Value("".getBytes()));

            writer.addMutation(m1);
            writer.addMutation(m2);
            writer.close();

        } catch (Exception e) {
            fail("failed on setup with exception", e);
        }
    }

    @Test
    public void stringSecurityCellAll() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloDBResultReader<SecurityStringValueCell> reader =
                new AccumuloDBResultReader<SecurityStringValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityStringValueCell>> items =
                reader.read(localParams, AccumuloCellTransformers.stringValueQualToLabelWithTime_ColVis_ColFam());
        assertNotNull(items);
        assertEquals(items.size(), 2);
        try {
            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");

            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");
            assertEquals(CellReflector.getColFam(cell), INFO_CF.toString());
            String colVis = CellReflector.getAuxiliaryValue(String.class, cell, "colvis");
            assertNotNull(colVis);
            assertEquals(colVis.length(), 0);
            long timestamp = auxExtractor.getTimestamp(cell, "timestamp");
            assertTrue(timestamp > 0l);
        }  catch (CellExtractorException e){
            fail("extraction error", e);
        }
    }

    @Test
    public void stringSecurityCellTimeColVis() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloDBResultReader<SecurityStringValueCell> reader =
                new AccumuloDBResultReader<SecurityStringValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityStringValueCell>> items = reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabelWithTime_ColVis());
        String colVis = null;
        long timestamp = 0l;
        try {
            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");

            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");
            colVis = CellReflector.getAuxiliaryValue(String.class, cell, "colvis");
            timestamp = auxExtractor.getTimestamp(cell, "timestamp");
            CellReflector.getColFam(cell);
            fail("null column family. shouldn't have made it this far");
        } catch (CellExtractorException e) {
            assertNotNull(colVis);
            assertTrue(timestamp > 0l);
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }

    }

    @Test
    public void stringSecurityCellColFam() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloDBResultReader<SecurityStringValueCell> reader =
                new AccumuloDBResultReader<SecurityStringValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityStringValueCell>> items = reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabelWithColFam());
        String colFam = null;

        try {
            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");

            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");
            colFam = CellReflector.getColFam(cell);
            CellReflector.getAuxiliaryValue(String.class, cell, "colvis");
            fail("missing col vis and timestamp. Shouldn't have made it this far");
        } catch (CellExtractorException e) {
            assertNotNull(colFam);
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }
    }

    @Test
    public void stringSecurityCellLabelValue() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloDBResultReader<SecurityStringValueCell> reader =
                new AccumuloDBResultReader<SecurityStringValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityStringValueCell>> items = reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());

        try {
            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");

            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");
            CellReflector.getColFam(cell);
            fail("null column fam. shouldn't have made it this far");
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }
    }

    @Test
    public void doubleSecurityCellLabelValue() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:gpa"}).build();
        AccumuloDBResultReader<SecurityDoubleValueCell> reader =
                new AccumuloDBResultReader<SecurityDoubleValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityDoubleValueCell>> items = reader.read(localParams, AccumuloCellTransformers.doubleValueQualtoLabel());

        try {
            double value = extractor.getDoubleValueByLabel(items.get(0).getInternalList(), "gpa");
            assertNotNull(value);
            assertEquals(value, 3.7d);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void longSecurityCellLabelValue() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:lm"}).build();
        AccumuloDBResultReader<SecurityLongValueCell> reader =
                new AccumuloDBResultReader<SecurityLongValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityLongValueCell>> items = reader.read(localParams, AccumuloCellTransformers.longValueQualtoLabel());

        try {
            long value = extractor.getLongValueByLabel(items.get(0).getInternalList(), "lm");
            assertNotNull(value);
            assertEquals(value, 12345l);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void byteSecurityCellLabelValue() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:name"}).build();
        AccumuloDBResultReader<SecurityByteValueCell> reader =
                new AccumuloDBResultReader<SecurityByteValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityByteValueCell>> items = reader.read(localParams, AccumuloCellTransformers.bytesValueQualtoLabel());

        try {
            byte[] value = extractor.getBytesValueByLabel(items.get(0).getInternalList(), "name");
            assertNotNull(value);
            assertEquals(value, "brian".getBytes());
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void intSecurityCellLabelValue() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:age"}).build();
        AccumuloDBResultReader<SecurityIntValueCell> reader =
                new AccumuloDBResultReader<SecurityIntValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<SecurityIntValueCell>> items = reader.read(localParams, AccumuloCellTransformers.intValueQualtoLabel());

        try {
            int value = extractor.getIntValueByLabel(items.get(0).getInternalList(), "age");
            assertNotNull(value);
            assertEquals(value, 30);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void singleValueAgg() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:age"}).build();
        AccumuloAggregateDBResultReader<IntValueCell> reader =
                new AccumuloAggregateDBResultReader<IntValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<IntValueCell>> items = reader.read(localParams, AccumuloCellTransformers.aggregateSingleQual("age"));
        assertNotNull(items);
        assertEquals(items.size(), 1);
        try {
            int value = extractor.getIntValueByLabel(items.get(0).getInternalList(), "age");
            assertNotNull(value);
            assertEquals(value, 59);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void multiValueAgg() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:age", "info:siblings"}).build();
        AccumuloAggregateDBResultReader<IntValueCell> reader =
                new AccumuloAggregateDBResultReader<IntValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<IntValueCell>> items = reader.read(localParams, AccumuloCellTransformers.aggregateMultiQual("age", "siblings"));
        assertNotNull(items);
        assertEquals(items.size(), 1);
        try {
            int ageCount = extractor.getIntValueByLabel(items.get(0).getInternalList(), "age");
            assertEquals(ageCount, 59);
            int siblingCount = extractor.getIntValueByLabel(items.get(0).getInternalList(), "siblings");
            assertEquals(siblingCount, 9);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void singleValueMean() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info:age"}).build();
        AccumuloAggregateDBResultReader<DoubleValueCell> reader =
                new AccumuloAggregateDBResultReader<DoubleValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<DoubleValueCell>> items = reader.read(localParams, AccumuloCellTransformers.averageSingleQual("age"));
        assertNotNull(items);
        assertEquals(items.size(), 1);
        try {
            double average = extractor.getDoubleValueByLabel(items.get(0).getInternalList(), "age");
            assertNotNull(average);
            assertEquals(average, 29.5);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void commonLabelFlatteningIfNecessary() {
       AccumuloParameters localParams = builder.setColumns(new String[]{"events", "info"}).build();
       AccumuloDBResultReader<SecurityStringValueCell> reader =
                new AccumuloDBResultReader<SecurityStringValueCell>(mockInstance);
        assertNotNull(reader);
        Map<String, String> commonLabels = new HashMap<String, String>();
        commonLabels.put("events", "event");
        List<CellGroup<SecurityStringValueCell>> items = reader.read(localParams, AccumuloCellTransformers.colFamToCommonLabelOnMatches(commonLabels));
        assertNotNull(items);
    }


    @Test
    public void countTotalKeyValues() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloAggregateDBResultReader<IntValueCell> reader =
                new AccumuloAggregateDBResultReader<IntValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<IntValueCell>> items = reader.read(localParams, AccumuloCellTransformers.totalKeyValueCount());
        assertNotNull(items);
        assertEquals(items.size(), 1);
        try {
            int value = extractor.getIntValueFromFirstCell(items.get(0).getInternalList());
            assertNotNull(value);
            assertEquals(value, 12);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

    @Test
    public void countDistinctRows() {
        AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
        AccumuloAggregateDBResultReader<IntValueCell> reader =
                new AccumuloAggregateDBResultReader<IntValueCell>(mockInstance);
        assertNotNull(reader);
        List<CellGroup<IntValueCell>> items = reader.read(localParams, AccumuloCellTransformers.distinctRowIDCount());
        assertNotNull(items);
        assertEquals(items.size(), 1);
        try {
            int value = extractor.getIntValueFromFirstCell(items.get(0).getInternalList());
            assertNotNull(value);
            assertEquals(value, 2);
        } catch (Exception e) {
            fail("failed on exception",e);
        }
    }

}
