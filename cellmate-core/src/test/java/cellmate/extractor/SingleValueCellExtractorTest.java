package cellmate.extractor;

import cellmate.cell.*;
import cellmate.cell.CellReflector;
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

    Tuple<StringValueCell> row1Tuple;
    Tuple<StringValueCell> row2Tuple;
    private SingleMultiValueCellExtractor extractorSingle =
            new SingleMultiValueCellExtractor();

    @BeforeClass
    public void hydrateTuple(){
        row1Tuple = new Tuple<StringValueCell>("fakeId1");
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

        row1Tuple.addCell(cell1);
        row1Tuple.addCell(cell2);
        row1Tuple.addCell(cell3);
        row1Tuple.addCell(cell4);
        row1Tuple.addCell(cell5);
        row1Tuple.addCell(cell6);
        row1Tuple.addCell(cell7);
        row1Tuple.addCell(cell8);
        row1Tuple.addCell(cell9);
        row1Tuple.addCell(cell10);

        row2Tuple = new Tuple<StringValueCell>("fakeId2");
        cell1 = new StringValueCell("name", "brian");
        cell4 = new StringValueCell("trylong", "error");
        cell5 = new StringValueCell("edge", "192");
        cell6 = new StringValueCell("edge", "error");
        cell7 = new StringValueCell("chk_t", "00-33-11");
        cell8 = new StringValueCell("chk_t", "00-33-11");
        cell9 = new StringValueCell("chk_d", "2012-08-22", 238124123l);
        cell10 = new StringValueCell("loc", "21911", 238124123l);
        StringValueCell cell11 = new StringValueCell("trydouble", "error");
        row2Tuple.addCell(cell1);
        row2Tuple.addCell(cell2);
        row2Tuple.addCell(cell3);
        row2Tuple.addCell(cell4);
        row2Tuple.addCell(cell5);
        row2Tuple.addCell(cell6);
        row2Tuple.addCell(cell7);
        row2Tuple.addCell(cell8);
        row2Tuple.addCell(cell9);
        row2Tuple.addCell(cell10);
        row2Tuple.addCell(cell11);
    }

    @Test
    public void tupleCounts() {
        assertEquals(row1Tuple.getInternalList().size(), 10);
        assertEquals(row2Tuple.getInternalList().size(), 11);
    }

    @Test
    public void filter() {
        Collection<StringValueCell> cells = extractorSingle.filterCellsByLabel(row1Tuple.getInternalList(), "name");
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
            Tuple<StringValueCell> tuple = new Tuple<StringValueCell>("blah");
            StringValueCell cell = new StringValueCell("name", "brian");
            tuple.addCell(cell);
            String value = extractorSingle.getStringValueByLabel(tuple.getInternalList(), "name");
            assertEquals(value, "brian");
        } catch (RuntimeException e){
            fail(e.getMessage(),e);
        } catch (CellExtractorException e) {
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void regexString() {  //startsWith 'l'
        Collection<StringValueCell> cells = extractorSingle.regexMatchLabel(row1Tuple.getInternalList(), "^[l]+[a-zA-Z]+");
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
            Tuple<DoubleValueCell> tupleBag = new Tuple<DoubleValueCell>("fake4id");
            DoubleValueCell cell1 = new DoubleValueCell("lat", 11.22);
            tupleBag.addCell(cell1);
            double lat = extractorSingle.getDoubleValueByLabel(tupleBag.getInternalList(), "lat");
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
            Tuple<IntValueCell> tupleBag = new Tuple<IntValueCell>("fake4id");
            IntValueCell cell1 = new IntValueCell("loc", 21911);
            tupleBag.addCell(cell1);
            int loc = extractorSingle.getIntValueByLabel(tupleBag.getInternalList(), "loc");
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
            Tuple<LongValueCell> tupleBag = new Tuple<LongValueCell>("fake4id");
            LongValueCell cell1 = new LongValueCell("loc", 21911l);
            tupleBag.addCell(cell1);
            long loc = extractorSingle.getLongValueByLabel(tupleBag.getInternalList(), "loc");
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
            Tuple<ByteValueCell> tupleBag = new Tuple<ByteValueCell>("fake4id");
            ByteValueCell cell1 = new ByteValueCell("loc", "21911".getBytes());
            tupleBag.addCell(cell1);
            byte[] loc = extractorSingle.getBytesValueByLabel(tupleBag.getInternalList(), "loc");
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
            List<String> items = extractorSingle.getAllStringCellValuesWithLabel(row1Tuple.getInternalList(), "event");
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
            Tuple<IntValueCell> tuple = new Tuple<IntValueCell>("fake4id");
            IntValueCell cell1 = new IntValueCell("edge", 191);
            IntValueCell cell2 = new IntValueCell("edge", 192);
            tuple.addCell(cell1);
            tuple.addCell(cell2);

            Collection<Integer> items = extractorSingle.getAllIntCellValuesWithLabel(tuple.getInternalList(), "edge");
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
            Tuple<LongValueCell> tupleBag = new Tuple<LongValueCell>("fake4id");
            LongValueCell cell1 = new LongValueCell("event", 234211213l);
            LongValueCell cell2 = new LongValueCell("event", 234111200l);
            tupleBag.addCell(cell1);
            tupleBag.addCell(cell2);

            List<Long> items = extractorSingle.getAllLongCellValueByLabel(tupleBag.getInternalList(), "event");
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
            extractorSingle.getLongValueByLabel(row2Tuple.getInternalList(), "trylong");
            fail(msg);
        }  catch (RuntimeException e) {
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
            extractorSingle.getDoubleValueByLabel(row2Tuple.getInternalList(), "trydouble");
            fail(msg);
        }  catch (RuntimeException e) {
            assertEquals(e.getCause().getClass(), ClassCastException.class);
            assertTrue(e.getMessage().contains("Unable to cast field value as instance"));
        }  catch (Exception e){
            fail(e.getMessage(),e);
        }
    }


}
