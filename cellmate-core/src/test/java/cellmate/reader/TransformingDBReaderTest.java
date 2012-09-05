package cellmate.reader;

import cellmate.cell.CellReflector;
import cellmate.cell.StringValueCell;
import cellmate.cell.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;
import static org.testng.Assert.*;

import java.util.Collection;
import java.util.List;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:47 PM
 */
public class TransformingDBReaderTest {

    private DBResultReader<MockDBResult, StringValueCell> reader;
    List<MockDBResult> dbResults;

    @BeforeClass
    public void setup() {
        reader = new TupleTransformerDBResultReader<MockDBResult, StringValueCell>();
        dbResults = Lists.newArrayList();
        dbResults.add(new MockDBResult("row1", "cf", "name", "brian"));
        dbResults.add(new MockDBResult("row1", "cf", "age", "13"));
        dbResults.add(new MockDBResult("row2", "cf", "name", "fred"));
        dbResults.add(new MockDBResult("row2", "events", "112231", "", 11l));
    }

    @Test
    public void qualValCells() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {
            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                } else if (!tuple.getTag().equals(dbItem.getId())){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };

        CommonReadParameters parameters = new CommonReadParameters.Builder().build();

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 2);
    }

    @Test
    public void commonLabelForAllCells() {
        CellTransformer<MockDBResult,StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                } else if (!tuple.getTag().equals(dbItem.getId())){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                }
                String label = dbItem.getQual();
                String value = dbItem.getVal();
                if(dbItem.getColfam().equals("events")) {
                    label = "event";
                    value = dbItem.getQual();
                }
                StringValueCell cell = new StringValueCell(label, value, dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };

        CommonReadParameters parameters = new CommonReadParameters.Builder().build();
        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 2);

        boolean foundEvent = false;
        boolean foundRow2 = false;
        for(Tuple<StringValueCell> tuple : tuples){
            if(tuple.getTag().equals("row2")){
                foundRow2 = true;
                for(StringValueCell cell : tuple.getInternalList()){
                    String label = CellReflector.getLabelAsString(cell);
                    if(label.equals("event")){
                        foundEvent = true;
                    }
                }
            }
        }
        assertTrue(foundEvent & foundRow2);
    }

    @Test
    public void allInOneTuple() {

        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    tuple = new Tuple<StringValueCell>("common bag label");
                }
                String label = dbItem.getQual();
                String value = dbItem.getVal();
                if(dbItem.getColfam().equals("events")) {
                    label = "event";
                    value = dbItem.getQual();
                }
                StringValueCell cell = new StringValueCell(label, value, dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };
        CommonReadParameters parameters = new CommonReadParameters.Builder().build();

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 1);
        assertEquals(tuples.iterator().next().getTag(), "common bag label");
    }

    @Test
    public void nullTupleHandling() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    return null;
                }
                return tuple;
            }
        };
        CommonReadParameters parameters = new CommonReadParameters.Builder().build();
        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 0);

        transformer = new CellTransformer<MockDBResult, StringValueCell> () {

            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                } else if (!tuple.getTag().equals(dbItem.getId())){
                    return null;
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };
        tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 1);
    }

    @Test
    public void maxResults() {
        CellTransformer<MockDBResult, StringValueCell> transformer = new CellTransformer<MockDBResult, StringValueCell> () {
            public Tuple<StringValueCell> apply(MockDBResult dbItem, Tuple<StringValueCell> tuple) {
                if(tuple == null){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                } else if (!tuple.getTag().equals(dbItem.getId())){
                    tuple = new Tuple<StringValueCell>(dbItem.getId());
                }
                StringValueCell cell = new StringValueCell(dbItem.getQual(), dbItem.getVal(), dbItem.getTimestamp());
                tuple.addCell(cell);
                return tuple;
            }
        };

        CommonReadParameters parameters = new CommonReadParameters.Builder().setMaxResults(1).build();

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults, parameters, transformer);
        assertEquals(tuples.size(), 1);
        assertNotNull(tuples.iterator().next().getInternalList());
        assertEquals(tuples.iterator().next().getInternalList().size(), 1);
    }

    @Test
    public void unsupportedRead() {
        try {

            CommonReadParameters parameters = new CommonReadParameters.Builder().setMaxResults(1).build();

            reader.read(dbResults, parameters);
            fail("should have throw unsupported operations exception");
        } catch (UnsupportedOperationException e){
            assertTrue(e.getMessage().contains("Read does not work without transformer."));
        } catch (Exception e){
            fail();
        }
    }
}
