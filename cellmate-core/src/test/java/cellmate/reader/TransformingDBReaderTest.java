package cellmate.reader;

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
        dbResults = Lists.newArrayList();
        dbResults.add(new MockDBResult("row1", "cf", "name", "brian"));
        dbResults.add(new MockDBResult("row1", "cf", "age", "13"));
        dbResults.add(new MockDBResult("row2", "cf", "name", "fred"));
        dbResults.add(new MockDBResult("row2", "events", "112231", "", 11l));
    }

    @Test
    public void qualValCells() {
        List<CellTransformer<MockDBResult, StringValueCell>> transformers = Lists.newArrayList();
        transformers.add(new CellTransformer<MockDBResult, StringValueCell> () {

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
        });
        reader = new TransformingDBResultToTupleReader<MockDBResult, StringValueCell>(transformers);

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults);
        assertEquals(tuples.size(), 2);
    }

    @Test
    public void commonLabelForAllCells() {
        List<CellTransformer<MockDBResult, StringValueCell>> transformers = Lists.newArrayList();
        transformers.add(new CellTransformer<MockDBResult, StringValueCell> () {

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
        });
        reader = new TransformingDBResultToTupleReader<MockDBResult, StringValueCell>(transformers);

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults);
        assertEquals(tuples.size(), 2);

        //check for item contents.
    }

    @Test
    public void allInOneTuple() {
        List<CellTransformer<MockDBResult, StringValueCell>> transformers = Lists.newArrayList();
        transformers.add(new CellTransformer<MockDBResult, StringValueCell> () {

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
        });
        reader = new TransformingDBResultToTupleReader<MockDBResult, StringValueCell>(transformers);

        Collection<Tuple<StringValueCell>> tuples = reader.read(dbResults);
        assertEquals(tuples.size(), 1);
    }

    @Test
    public void nullReturnHandling() {
         //see how the reader reacts to null returns.
    }

    @Test
    public void maxResults() {
        //call reader.read with max results.
    }
}
