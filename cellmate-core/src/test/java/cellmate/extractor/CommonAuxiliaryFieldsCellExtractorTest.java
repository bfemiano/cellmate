package cellmate.extractor;

import cellmate.cell.CellReflector;
import cellmate.cell.StringValueCell;
import cellmate.cell.Tuple;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 2:21 PM
 */
public class CommonAuxiliaryFieldsCellExtractorTest {

    private Tuple<StringValueCell> tuple = new Tuple<StringValueCell>("fortesting");
    private CommonAuxiliaryFieldsCellExtractor timestampHelper = new CommonAuxiliaryFieldsCellExtractor();
    StringValueCell tsCell1;
    StringValueCell tsCell2;
    StringValueCell tsCell3;

    @BeforeClass
    public void setup() {
        tsCell1 = new StringValueCell("name", "brian", 11l);
        tsCell2 = new StringValueCell("name", "brian", 22l);
        tsCell3 = new StringValueCell("name", "brian");
        tuple.addCell(tsCell1);
        tuple.addCell(tsCell2);
        tuple.addCell(tsCell3);
    }

    @Test
    public void getCellsWithTimestamp() {
        Collection<StringValueCell> cells = timestampHelper.getCellsWithTimestamp(tuple.getInternalList(), "ts");
        assertEquals(cells.size(), 3);
        for(StringValueCell cell : cells){
            assertEquals(CellReflector.getValueAsString(cell), "brian");
        }
    }

    @Test
    public void getTimestamp() {

        long timestamp = timestampHelper.getTimestamp(tsCell1, "ts");
        assertEquals(timestamp, 11l);

        try {
            timestamp = timestampHelper.getTimestamp(tsCell3, "timestamp");
            fail("should not have found timestamp");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("No field matching cell auxiliary fields with given name: timestamp"));
        }
    }

    @Test(dependsOnMethods = "getTimestamp")
    public void getLatestTimestamp() {
        StringValueCell cell = timestampHelper.getCellWithMostRecentTimestamp(tuple.getInternalList(), "ts");
        assertNotNull(cell);
        assertEquals(timestampHelper.getTimestamp(cell, "ts"), new Long(22l));
    }
}
