package cellmate.extractor;

import cellmate.cell.StringValueCell;
import cellmate.cell.CellGroup;
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

    private CellGroup<StringValueCell> cellGroup = new CellGroup<StringValueCell>("fortesting");
    private CommonAuxiliaryFieldsCellExtractor timestampHelper = new CommonAuxiliaryFieldsCellExtractor();
    StringValueCell tsCell1;
    StringValueCell tsCell2;
    StringValueCell tsCell3;

    @BeforeClass
    public void setup() {
        tsCell1 = new StringValueCell("name", "brian", 11l);
        tsCell2 = new StringValueCell("name", "brian", 22l);
        tsCell3 = new StringValueCell("name", "brian");
        cellGroup.addCell(tsCell1);
        cellGroup.addCell(tsCell2);
        cellGroup.addCell(tsCell3);
    }

    @Test
    public void getCellsWithTimestamp() {
        Collection<StringValueCell> cells = timestampHelper.getCellsWithTimestamp(cellGroup.getInternalList(), "ts");
        assertEquals(cells.size(), 3);
        try {
            for(StringValueCell cell : cells){
                assertEquals(CellReflector.getValueAsString(cell), "brian");
            }
        }  catch (Exception e){
            fail("failed on exception", e);
        }
    }

    @Test
    public void getTimestamp() {


        long timestamp = 0;
        try {
            timestamp = timestampHelper.getTimestamp(tsCell1, "ts");
        } catch (Exception e) {
            fail("failed on exception", e);
        }
        assertEquals(timestamp, 11l);

        try {
            timestamp = timestampHelper.getTimestamp(tsCell3, "timestamp");
            fail("should not have found timestamp");
        } catch (CellExtractorException e) {
            assertTrue(e.getMessage().contains("No field matching cell auxiliary fields with given name: timestamp"));
        }
    }

    @Test(dependsOnMethods = "getTimestamp")
    public void getLatestTimestamp() {
        try {
            StringValueCell cell = timestampHelper.getCellWithMostRecentTimestamp(cellGroup.getInternalList(), "ts");
            assertNotNull(cell);
            assertEquals(timestampHelper.getTimestamp(cell, "ts"), new Long(22l));
        } catch (Exception e){
            fail("failed on exception", e);
        }
    }
}
