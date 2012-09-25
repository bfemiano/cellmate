package cellmate.accumulo.reader;

import cellmate.accumulo.cell.SecurityStringValueCell;
import cellmate.accumulo.parameters.AccumuloParameters;
import cellmate.accumulo.reader.celltransformer.AccumuloCellTransformers;
import cellmate.accumulo.reader.celltransformer.SecurityStringCellTransformer;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.CommonParameters;
import cellmate.extractor.*;
import org.apache.accumulo.core.client.*;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.iterators.user.RegExFilter;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.hadoop.io.Text;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 9/10/12
 * Time: 10:28 PM
 */
public class AccumuloReaderTest {

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
    private static final Text HEIGHT = new Text("height");
    private static final Text EVENT1 = new Text("2342");
    private static final Text EVENT2 = new Text("2343");

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
            m1.put(INFO_CF, AGE, new Value("30".getBytes()));
            m1.put(INFO_CF, HEIGHT, new Value("6ft".getBytes()));
            m1.put(EVENTS_CF, EVENT1, new Value("".getBytes()));
            m1.put(EVENTS_CF, EVENT2, new Value("".getBytes()));

            Mutation m2 = new Mutation("row2");
            m2.put(INFO_CF, NAME, new Value("adam".getBytes()));
            m2.put(INFO_CF, AGE, new Value("29".getBytes()));
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
    public void readParameters() {

        parameters = builder.build();

        String user = parameters.getUser();
        assertEquals(user, "user1");

        String password = parameters.getPassword();
        assertEquals(password, "password");

        String instanceName = parameters.getInstanceName();
        assertEquals(instanceName, "testInstance");

        String zookeepers = parameters.getZookeepers();
        assertEquals(zookeepers, "localhost:2181");

        int maxResults = parameters.getMaxResults();
        assertEquals(maxResults, 100);
    }

    @Test
    public void readerTypeValidation() {
        try {
            CommonParameters badParameters = new CommonParameters.Builder().build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            reader.read(badParameters, AccumuloCellTransformers.stringValueQualtoLabel());
            fail("should have throw illegal argument");
        } catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("ReadParameter implementation must be cellmate.accumulo.parameters.AccumuloParameters"));
        } catch (Exception e){
            fail("general exception failure",e);
        }

        try {
            parameters = builder.setZookeepers(null).setInstanceName(null).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
        } catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("missing zookeepers and/or instance id"));
        }
    }

    @Test
    public void multiFamilyGetAsStrings(){

        try {
            AccumuloParameters localParams = builder.setColumns(new String[]{"info", "events"}).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            List<CellGroup<SecurityStringValueCell>> items =
                    reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabelWithColFam());
            assertNotNull(items);
            assertEquals(items.size(), 2);
            assertEquals(items.get(0).getInternalList().size(), 5);
            assertEquals(items.get(1).getInternalList().size(), 4);

        } catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void iteratorAttachment() {
        try {

            IteratorSetting iter = new IteratorSetting(15, "regexfilter", RegExFilter.class);
            iter.addOption(RegExFilter.VALUE_REGEX, "brian");
            AccumuloParameters localParams = builder.setColumns(new String[]{"info:name"}).
                    addIteratorSetting(iter).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            List<CellGroup<SecurityStringValueCell>> items =
                    reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            assertNotNull(items);
            assertEquals(items.size(), 1);

            SingleMultiValueCellExtractor extractor = new SingleMultiValueCellExtractor();
            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");
            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");

        } catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void singleFamilyGetAsStrings() {
        try {
            AccumuloParameters localParams = builder.setColumns(new String[]{"info"}).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            List<CellGroup<SecurityStringValueCell>> items =
                    reader.read(localParams, AccumuloCellTransformers.stringValueQualToLabelWithTime_ColVis_ColFam());
            assertNotNull(items);
            assertEquals(items.size(), 2);
            assertEquals(items.get(0).getInternalList().size(), 3);
            assertEquals(items.get(1).getInternalList().size(), 3);

            SingleMultiValueCellExtractor extractor = new SingleMultiValueCellExtractor();
            CommonAuxiliaryFieldsCellExtractor auxExtractor = new CommonAuxiliaryFieldsCellExtractor();

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

        } catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void restrictedQuals() {
        try {
            AccumuloParameters localParams = builder.setColumns(new String[]{"info:name"}).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            List<CellGroup<SecurityStringValueCell>> items =
                    reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            assertNotNull(items);
            assertEquals(items.size(), 2);
            assertEquals(items.get(0).getInternalList().size(), 1);
            assertEquals(items.get(1).getInternalList().size(), 1);

            SingleMultiValueCellExtractor extractor = new SingleMultiValueCellExtractor();
            CommonAuxiliaryFieldsCellExtractor auxExtractor = new CommonAuxiliaryFieldsCellExtractor();

            SecurityStringValueCell cell = extractor.getSingleCellByLabel(items.get(0).getInternalList(), "name");

            String value = CellReflector.getValueAsString(cell);
            assertNotNull(value);
            assertEquals(value, "brian");
            assertEquals(CellReflector.getColFam(cell), INFO_CF.toString());

            extractor.getSingleCellByLabel(items.get(0).getInternalList(), "age");
            fail("should not have extracted. age does not exist in this scan");
        } catch (CellExtractorException e) {
            if(e.getType().equals(ErrorType.MISSING_FIELD)) {
                assertTrue(e.getMessage().contains("No value for single get"));
            }
        } catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void testRange() {
        try {
            AccumuloParameters localParams = builder.
                    setColumns(new String[]{"info"}).
                    setStartKey("row1").
                    setEndKey("row1").build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            List<CellGroup<SecurityStringValueCell>> items =
                    reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            assertNotNull(items);
            assertEquals(items.size(), 1);
            assertEquals(items.get(0).getInternalList().size(), 3);
            assertEquals(items.get(0).getTag(), "row1");

        } catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void missingTable() {
        try {
            AccumuloParameters localParams = builder.setTable(null).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            fail("reader should complain there is no table");
        } catch (IllegalArgumentException e){
            assertEquals(e.getMessage(), "Missing table name in parameters");
        }catch (Exception e){
            fail("failed with exception",e);
        }
    }



    @Test
    public void missingUser() {

        try {
            AccumuloParameters localParams = builder.setUser(null).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            fail("reader should complain there is no user");
        } catch (IllegalArgumentException e){
            assertEquals(e.getMessage(), "missing user/pass");
        }catch (Exception e){
            fail("failed with exception",e);
        }
    }

    @Test
    public void malforedColFamEntry() {
        try {
            AccumuloParameters localParams = builder.setColumns(new String[]{"cf:blah:blah"}).build();
            AccumuloDBResultReader reader =
                    new AccumuloDBResultReader(mockInstance);
            assertNotNull(reader);
            reader.read(localParams, AccumuloCellTransformers.stringValueQualtoLabel());
            fail("reader should complain about malformed column family");
        } catch (IllegalArgumentException e){
            assertEquals(e.getMessage(), "malformed colFam entry: cf:blah:blah");
        }catch (Exception e){
            fail("failed with exception",e);
        }
    }

}
