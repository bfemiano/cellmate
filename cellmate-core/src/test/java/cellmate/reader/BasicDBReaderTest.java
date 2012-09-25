package cellmate.reader;

import cellmate.cell.CellGroup;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import cellmate.cell.StringValueCell;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import static org.testng.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:47 PM
 */
public class BasicDBReaderTest {

    private DBResultReader<MockDBResult> reader;
    List<MockDBResult> dbResults;

    @BeforeClass
    public void setup() {
        reader = new BasicCellGroupingDBResultReader<MockDBResult>();
        dbResults = Lists.newArrayList();
        dbResults.add(new MockDBResult("row1", "cf", "name", "brian"));
        dbResults.add(new MockDBResult("row1", "cf", "age", "13"));
        dbResults.add(new MockDBResult("row2", "cf", "name", "fred"));
        dbResults.add(new MockDBResult("row2", "events", "112231", "", 11l));
    }

    @Test
    public void qualValCells() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {
            public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> tuple) {
                if (!tuple.getTag().equals(dbItem.getId())){
                    tuple = new CellGroup<StringValueCell>(dbItem.getId());
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };

        MockParameters parameters = new MockParameters.Builder().build();
        Collection<CellGroup<StringValueCell>> cellGroups = reader.read(dbResults, parameters, transformer);
        assertEquals(cellGroups.size(), 2);
    }

    @Test
    public void commonLabelForAllCells() {

        try {
            CellTransformer<MockDBResult,StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

                public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> tuple) {
                    if (!tuple.getTag().equals(dbItem.getId())){
                        tuple = new CellGroup<StringValueCell>(dbItem.getId());
                    }
                    String label = dbItem.getQual();
                    String value = dbItem.getVal();
                    if(dbItem.getColFam().equals("events")) {
                        label = "event";
                        value = dbItem.getQual();
                    }
                    StringValueCell cell = new StringValueCell(label, value, dbItem.getTimestamp());
                    tuple.addCell(cell);
                    return tuple;
                }
            };

            MockParameters parameters = new MockParameters.Builder().build();
            Collection<CellGroup<StringValueCell>> cellGroups = reader.read(dbResults, parameters, transformer);
            assertEquals(cellGroups.size(), 2);

            boolean foundEvent = false;
            boolean foundRow2 = false;
            for(CellGroup<StringValueCell> cellGroup : cellGroups){
                if(cellGroup.getTag().equals("row2")){
                    foundRow2 = true;
                    for(StringValueCell cell : cellGroup.getInternalList()){
                        String label = CellReflector.getLabelAsString(cell);
                        if(label.equals("event")){
                            foundEvent = true;
                        }
                    }
                }
            }
            assertTrue(foundEvent & foundRow2);
        } catch (CellExtractorException e){
            fail();
        }

    }

    @Test
    public void allInOneGroup() {

        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> group) {

                if(!dbItem.getId().equals(group.getTag()))
                    group = new CellGroup<StringValueCell>("common bag label");
                String label = dbItem.getQual();
                String value = dbItem.getVal();
                if(dbItem.getColFam().equals("events")) {
                    label = "event";
                    value = dbItem.getQual();
                }
                StringValueCell cell = new StringValueCell(label, value, dbItem.getTimestamp());
                group.addCell(cell);
                return group;
            }
        };
        MockParameters parameters = new MockParameters.Builder().build();
        Collection<CellGroup<StringValueCell>> cellGroups = reader.read(dbResults, parameters, transformer);
        assertEquals(cellGroups.size(), 1);
        assertEquals(cellGroups.iterator().next().getTag(), "common bag label");
    }

    @Test
    public void commonCellGroupingErrors() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> group) {
                return group;  //never changes default tuple.
            }
        };
        MockParameters parameters = new MockParameters.Builder().build();
        Collection<CellGroup<StringValueCell>> cellGroups = reader.read(dbResults, parameters, transformer);
        assertEquals(cellGroups.size(), 0);

        transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> cellGroup) {
                if (!cellGroup.getTag().equals(dbItem.getId())){
                    return null;  //null group
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                cellGroup.addCell(cell);
                return cellGroup;
            }
        };
        try {
            reader.read(dbResults, parameters, transformer);
            fail("null group returned. should have failed. ");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Supplied cell transformer returned a null cell group reference"));
        }

    }

    @Test
    public void maxResults() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {
            public CellGroup<StringValueCell> apply(MockDBResult dbItem, CellGroup<StringValueCell> tuple) {
                if (!tuple.getTag().equals(dbItem.getId())){
                    tuple = new CellGroup<StringValueCell>(dbItem.getId());
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };

        MockParameters parameters = new MockParameters.Builder().setMaxResults(1).build();
        Collection<CellGroup<StringValueCell>> cellGroups = reader.read(dbResults, parameters, transformer);
        assertEquals(cellGroups.size(), 1);
        assertNotNull(cellGroups.iterator().next().getInternalList());
        assertEquals(cellGroups.iterator().next().getInternalList().size(), 1);
    }

    @Test
    public void parameterDelegation() {
        MockParameters parameters = new MockParameters.Builder().addNamedProperty("TEST_STRING", "hi").build();
        try {
            String propValue = parameters.getString("TEST_STRING");
            assertEquals(propValue, "hi");
        } catch (NoSuchElementException e){
            fail("didn't find the property");
        }

        MockDBResult result = new MockDBResult("id1", "colFam", "qual", "val");

        parameters = new MockParameters.Builder().addNamedProperty("MOCK_RESULT", result).build();
        try {
            MockDBResult propValue = parameters.getObjectAs(MockDBResult.class, "MOCK_RESULT");
            assertEquals(propValue.getId(), "id1");
        } catch (NoSuchElementException e){
            fail("didn't find the property");
        }
    }
}
