package cellmate.extractor;

import cellmate.cell.*;
import com.google.common.base.Predicate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 8/26/12
 * Time: 9:36 PM
 */
public class SingleValueCellExtractorTest {

    CellGroup<StringValueCell> row1CellGroup;
    CellGroup<StringValueCell> row2CellGroup;
    private SingleMultiValueCellExtractor extractorSingle =
            new SingleMultiValueCellExtractor();

    @BeforeClass
    public void hydrateTuple(){
        row1CellGroup = new CellGroup<StringValueCell>("fakeId1");
        StringValueCell cell1 = new StringValueCell("name", "brian");
        StringValueCell cell2 = new StringValueCell("event", "234211213");
        StringValueCell cell3 = new StringValueCell("event", "234111200");
        StringValueCell cell4 = new StringValueCell("edge", "191");
        StringValueCell cell5 = new StringValueCell("edge", "192");
        StringValueCell cell6 = new StringValueCell("chk_t", "00-33-11");
        StringValueCell cell7 = new StringValueCell("ckk_d", "2012-08-22", 238124123l);
        StringValueCell cell8 = new StringValueCell("loc", "21911", 238124123l);
        StringValueCell cell9 = new StringValueCell("lat", "11.22");
        StringValueCell cell10 = new StringValueCell("lon", "33.44");

        row1CellGroup.addCell(cell1);
        row1CellGroup.addCell(cell2);
        row1CellGroup.addCell(cell3);
        row1CellGroup.addCell(cell4);
        row1CellGroup.addCell(cell5);
        row1CellGroup.addCell(cell6);
        row1CellGroup.addCell(cell7);
        row1CellGroup.addCell(cell8);
        row1CellGroup.addCell(cell9);
        row1CellGroup.addCell(cell10);

        row2CellGroup = new CellGroup<StringValueCell>("fakeId2");
        cell1 = new StringValueCell("name", "brian");
        cell4 = new StringValueCell("trylong", "error");
        cell5 = new StringValueCell("edge", "192");
        cell6 = new StringValueCell("edge", "error");
        cell7 = new StringValueCell("chk_t", "00-33-11");
        cell8 = new StringValueCell("chk_t", "00-33-11");
        cell9 = new StringValueCell("chk_d", "2012-08-22", 238124123l);
        cell10 = new StringValueCell("loc", "21911", 238124123l);
        StringValueCell cell11 = new StringValueCell("trydouble", "error");
        row2CellGroup.addCell(cell1);
        row2CellGroup.addCell(cell2);
        row2CellGroup.addCell(cell3);
        row2CellGroup.addCell(cell4);
        row2CellGroup.addCell(cell5);
        row2CellGroup.addCell(cell6);
        row2CellGroup.addCell(cell7);
        row2CellGroup.addCell(cell8);
        row2CellGroup.addCell(cell9);
        row2CellGroup.addCell(cell10);
        row2CellGroup.addCell(cell11);
    }

    @Test
    public void tupleCounts() {
        assertEquals(row1CellGroup.getInternalList().size(), 10);
        assertEquals(row2CellGroup.getInternalList().size(), 11);
    }

    @Test
    public void filter() {
        Collection<StringValueCell> cells = extractorSingle.filterCellsByLabel(row1CellGroup.getInternalList(), "name");
        assertNotNull(cells);
        assertEquals(cells.size(), 1);
        try {
            assertEquals(CellReflector.getLabelAsString(cells.iterator().next()), "name");
            assertEquals(CellReflector.getValueAsString(cells.iterator().next()), "brian");
        }  catch (Exception e){
            fail();
        }
    }

    @Test
    public void exactString() {
        try {
            CellGroup<StringValueCell> cellGroup = new CellGroup<StringValueCell>("blah");
            StringValueCell cell = new StringValueCell("name", "brian");
            cellGroup.addCell(cell);
            String value = extractorSingle.getStringValueByLabel(cellGroup.getInternalList(), "name");
            assertEquals(value, "brian");
        } catch (RuntimeException e){
            fail(e.getMessage(),e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void regexString() {  //startsWith 'l'
        Collection<StringValueCell> cells = extractorSingle.regexMatchLabel(row1CellGroup.getInternalList(), "^[l]+[a-zA-Z]+");
        assertNotNull(cells);
        assertEquals(cells.size(), 3);

        boolean foundLat= false;
        boolean foundLon = false;
        boolean foundLoc = false;
        try {
            for(StringValueCell cell : cells) {
                if(CellReflector.getLabelAsString(cell).equals("lat"))
                    foundLat = true;
                if(CellReflector.getLabelAsString(cell).equals("lon"))
                    foundLon = true;
                if(CellReflector.getLabelAsString(cell).equals("loc"))
                    foundLoc = true;
            }
            assertTrue(foundLat & foundLon & foundLoc);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void exactDouble() {
        try {
            CellGroup<DoubleValueCell> cellGroupBag = new CellGroup<DoubleValueCell>("fake4id");
            DoubleValueCell cell1 = new DoubleValueCell("lat", 11.22);
            cellGroupBag.addCell(cell1);
            double lat = extractorSingle.getDoubleValueByLabel(cellGroupBag.getInternalList(), "lat");
            assertEquals(lat, 11.22);
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail("should have found non null double value");
        }
    }

    @Test
    public void exactInt() {
        try {
            CellGroup<IntValueCell> cellGroupBag = new CellGroup<IntValueCell>("fake4id");
            IntValueCell cell1 = new IntValueCell("loc", 21911);
            cellGroupBag.addCell(cell1);
            int loc = extractorSingle.getIntValueByLabel(cellGroupBag.getInternalList(), "loc");
            assertEquals(loc, 21911);
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void exactLong() {
        try {
            CellGroup<LongValueCell> cellGroupBag = new CellGroup<LongValueCell>("fake4id");
            LongValueCell cell1 = new LongValueCell("loc", 21911l);
            cellGroupBag.addCell(cell1);
            long loc = extractorSingle.getLongValueByLabel(cellGroupBag.getInternalList(), "loc");
            assertEquals(loc, 21911l);
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail("should have found non null long value");
        }
    }


    @Test
    public void exactBytes() {
        try {
            CellGroup<ByteValueCell> cellGroupBag = new CellGroup<ByteValueCell>("fake4id");
            ByteValueCell cell1 = new ByteValueCell("loc", "21911".getBytes());
            cellGroupBag.addCell(cell1);
            byte[] loc = extractorSingle.getBytesValueByLabel(cellGroupBag.getInternalList(), "loc");
            assertEquals(loc, "21911".getBytes());
        }  catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(),e);
        }
    }

    @Test
    public void stringList() {
        try {
            List<String> items = extractorSingle.getAllStringCellValuesWithLabel(row1CellGroup.getInternalList(), "event");
            assertNotNull(items);
            assertEquals(items.size(), 2);
            boolean found1 = false;
            boolean found2 = false;
            for(String item : items) {
                if(item.equals("234211213"))
                    found1 = true;
                if(item.equals("234111200"))
                    found2 = true;
            }
            assertTrue(found1 & found2);
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(),e);
        }
    }

    @Test
    public void intList() {
        try {
            CellGroup<IntValueCell> cellGroup = new CellGroup<IntValueCell>("fake4id");
            IntValueCell cell1 = new IntValueCell("edge", 191);
            IntValueCell cell2 = new IntValueCell("edge", 192);
            cellGroup.addCell(cell1);
            cellGroup.addCell(cell2);

            Collection<Integer> items = extractorSingle.getAllIntCellValuesWithLabel(cellGroup.getInternalList(), "edge");
            assertNotNull(items);
            assertEquals(items.size(), 2);
            boolean found1 = false;
            boolean found2 = false;
            for(Integer item : items) {
                if(item == 191)
                    found1 = true;
                if(item == 192)
                    found2 = true;
            }
            assertTrue(found1 & found2);

        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void longList() {
        try {
            CellGroup<LongValueCell> cellGroupBag = new CellGroup<LongValueCell>("fake4id");
            LongValueCell cell1 = new LongValueCell("event", 234211213l);
            LongValueCell cell2 = new LongValueCell("event", 234111200l);
            cellGroupBag.addCell(cell1);
            cellGroupBag.addCell(cell2);

            List<Long> items = extractorSingle.getAllLongCellValueByLabel(cellGroupBag.getInternalList(), "event");
            assertNotNull(items);
            assertEquals(items.size(), 2);
            boolean found1 = false;
            boolean found2 = false;
            for(Long item : items) {
                if(item == 234211213l)
                    found1 = true;
                if(item == 234111200l)
                    found2 = true;
            }
            assertTrue(found1 & found2);
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void castToLongError() {
        String msg = "row2 contains an event with an invalid long value, and should have thrown an error";
        try {
            extractorSingle.getLongValueByLabel(row2CellGroup.getInternalList(), "trylong");
            fail(msg);
        }  catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
            assertEquals(e.getCause().getClass(), ClassCastException.class);
            assertTrue(e.getMessage().contains("Unable to cast field value as instance"));
        }  catch (Exception e){
            fail(e.getMessage(),e);
        }
    }

    @Test
    public void castToDouble() {
        String msg = "row2 contains a lon with an invalid double value, and should have thrown an error";
        try {
            extractorSingle.getDoubleValueByLabel(row2CellGroup.getInternalList(), "trydouble");
            fail(msg);
        }  catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
            assertEquals(e.getCause().getClass(), ClassCastException.class);
            assertTrue(e.getMessage().contains("Unable to cast field value as instance"));
        }  catch (Exception e){
            fail(e.getMessage(),e);
        }
    }

    @Test
    public void testGetFirstItem() {
        CellGroup<StringValueCell> strGroup = new CellGroup<StringValueCell>("");
        strGroup.addCell(new StringValueCell("l", "test1"));

        CellGroup<IntValueCell> intGroup = new CellGroup<IntValueCell>("");
        intGroup.addCell(new IntValueCell("l", 1));

        CellGroup<LongValueCell> longGroup = new CellGroup<LongValueCell>("");
        longGroup.addCell(new LongValueCell("l", 2l));

        CellGroup<ByteValueCell> byteGroup = new CellGroup<ByteValueCell>("");
        byteGroup.addCell(new ByteValueCell("l", "test1".getBytes()));

        CellGroup<DoubleValueCell> doubleGroup = new CellGroup<DoubleValueCell>("");
        doubleGroup.addCell(new DoubleValueCell("l", 33.3d));

        try {
            assertEquals(extractorSingle.getStringValueFromFirstCell(strGroup.getInternalList()), "test1");
            assertEquals(extractorSingle.getIntValueFromFirstCell(intGroup.getInternalList()), 1);
            assertEquals(extractorSingle.getLongValueFromFirstCell(longGroup.getInternalList()), 2l);
            assertEquals(extractorSingle.getBytesValueFromFirstCell(byteGroup.getInternalList()), "test1".getBytes());
            assertEquals(extractorSingle.getDoubleValueFromFirstCell(doubleGroup.getInternalList()), 33.3d);
        } catch (Exception e) {
            fail("failed with exception",e);
        }

        doubleGroup = new CellGroup<DoubleValueCell>("");
        doubleGroup.addCell(new DoubleValueCell("l", 33.3d));
        doubleGroup.addCell(new DoubleValueCell("l", 44.6d));
        try {
            extractorSingle.getDoubleValueFromFirstCell(doubleGroup.getInternalList());
            fail("doublegroup has more than one value. Should have gone to exception");
        } catch (CellExtractorException e){
           assertEquals(e.getType(), ErrorType.TOO_MANY_FIELDS);
        } catch (Exception e){
            fail();
        }
    }



}
