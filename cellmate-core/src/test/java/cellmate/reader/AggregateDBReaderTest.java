package cellmate.reader;

import cellmate.cell.StringValueCell;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import java.util.List;

/**
 * User: bfemiano
 * Date: 9/11/12
 * Time: 11:34 PM
 */
public class AggregateDBReaderTest {

    private DBResultReader<MockDBResult> reader;
    List<MockDBResult> dbResults;

    @BeforeClass
    public void setup() {
        reader = new BasicCellGroupingDBResultReader<MockDBResult>();
        dbResults = Lists.newArrayList();
        dbResults.add(new MockDBResult("row1", "cf", "name", "brian"));
        dbResults.add(new MockDBResult("row1", "cf", "age", "13"));
        dbResults.add(new MockDBResult("row2", "cf", "name", "fred"));
        dbResults.add(new MockDBResult("row2", "cf", "age", "11"));
    }

    @Test
    public void testAdd() {

    }
}
