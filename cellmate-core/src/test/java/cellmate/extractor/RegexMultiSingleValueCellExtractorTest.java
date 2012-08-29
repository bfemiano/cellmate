package cellmate.extractor;

import cellmate.tuple.*;
import cellmate.tuple.cell.Cell;
import cellmate.tuple.cell.CellReflector;
import cellmate.tuple.cell.Label;
import cellmate.tuple.cell.Value;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 8/26/12
 * Time: 9:36 PM
 */
public class RegexMultiSingleValueCellExtractorTest {

    TupleBag<StringValueTuple> row1TupleBag;
    TupleBag<StringValueTuple> row2TupleBag;
    private StringMultiSingleValueCellExtractor extractorSingle =
            new StringMultiSingleValueCellExtractor();
    private TupleBag<StringValueTuple> row3TupleBag;

    @Cell
    public class LongValueTuple {
        @Label
        private String label;
        @Value
        private long value;

        public LongValueTuple(String label, long value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class IntValueTuple {
        @Label
        private String label;
        @Value
        private int value;

        public IntValueTuple(String label, int value) {
            this.label = label;
            this.value = value;
        }
    }

    @BeforeClass
    public void hydrateTuple(){
        row1TupleBag = new TupleBag<StringValueTuple>("fakeId1");
        StringValueTuple tuple1 = new StringValueTuple("name", "brian");
        StringValueTuple tuple2 = new StringValueTuple("event", "234211213");
        StringValueTuple tuple3 = new StringValueTuple("event", "234111200");
        StringValueTuple tuple4 = new StringValueTuple("edge", "191");
        StringValueTuple tuple5 = new StringValueTuple("edge", "192");
        StringValueTuple tuple6 = new StringValueTuple("chk_t", "00-33-11");
        StringValueTuple tuple7 = new StringValueTuple("ckk_d", "2012-08-22", 238124123l);
        StringValueTuple tuple8 = new StringValueTuple("loc", "21911", 238124123l);
        StringValueTuple tuple9 = new StringValueTuple("lat", "11.22");
        StringValueTuple tuple10 = new StringValueTuple("lon", "33.44");

        row1TupleBag.addCellTuple(tuple1);
        row1TupleBag.addCellTuple(tuple2);
        row1TupleBag.addCellTuple(tuple3);
        row1TupleBag.addCellTuple(tuple4);
        row1TupleBag.addCellTuple(tuple5);
        row1TupleBag.addCellTuple(tuple6);
        row1TupleBag.addCellTuple(tuple7);
        row1TupleBag.addCellTuple(tuple8);
        row1TupleBag.addCellTuple(tuple9);
        row1TupleBag.addCellTuple(tuple10);

        row2TupleBag = new TupleBag<StringValueTuple>("fakeId2");
        tuple1 = new StringValueTuple("name", "brian");
        tuple4 = new StringValueTuple("trylong", "error");
        tuple5 = new StringValueTuple("edge", "192");
        tuple6 = new StringValueTuple("edge", "error");
        tuple7 = new StringValueTuple("chk_t", "00-33-11");
        tuple8 = new StringValueTuple("chk_t", "00-33-11");
        tuple9 = new StringValueTuple("chk_d", "2012-08-22", 238124123l);
        tuple10 = new StringValueTuple("loc", "21911", 238124123l);
        StringValueTuple tuple11 = new StringValueTuple("trydouble", "error");
        row2TupleBag.addCellTuple(tuple1);
        row2TupleBag.addCellTuple(tuple2);
        row2TupleBag.addCellTuple(tuple3);
        row2TupleBag.addCellTuple(tuple4);
        row2TupleBag.addCellTuple(tuple5);
        row2TupleBag.addCellTuple(tuple6);
        row2TupleBag.addCellTuple(tuple7);
        row2TupleBag.addCellTuple(tuple8);
        row2TupleBag.addCellTuple(tuple9);
        row2TupleBag.addCellTuple(tuple10);
        row2TupleBag.addCellTuple(tuple11);

        row3TupleBag = new TupleBag<StringValueTuple>("fakeId3");
        row3TupleBag.addCellTuple(new StringValueTuple("event", "e1", 111111l));
        row3TupleBag.addCellTuple(new StringValueTuple("event", "e2", 222222l));
        row3TupleBag.addCellTuple(new StringValueTuple("event", "e3", 333333l));
    }

    @Test
    public void tupleCounts() {
        assertEquals(row1TupleBag.getInternalList().size(), 10);
        assertEquals(row2TupleBag.getInternalList().size(), 11);
    }

    @Test
    public void testMostRecentTimestamp() {
        StringValueTuple mostRecent = extractorSingle.getMostRecentTimestamp(row3TupleBag.getInternalList());
        assertNotNull(mostRecent);
        try {
            assertEquals(CellReflector.getLabelAsString(mostRecent), "event");
            assertEquals(CellReflector.getValueAsString(mostRecent), "e3");
            long time = CellReflector.getAuxiliaryValue(Long.class, mostRecent, "ts");
            assertEquals(time , 333333l);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void exactString() {
        List<StringValueTuple> tuples = extractorSingle.matchLabel(row1TupleBag.getInternalList(), "name");
        assertNotNull(tuples);
        assertEquals(tuples.size(), 1);
        try {
            assertEquals(CellReflector.getLabelAsString(tuples.get(0)), "name");
            assertEquals(CellReflector.getValueAsString(tuples.get(0)), "brian");
        }  catch (Exception e){
            fail();
        }
    }

    @Test
    public void regexString() {  //startsWith 'l'
        List<StringValueTuple> tuples = extractorSingle.regexMatchLabel(row1TupleBag.getInternalList(), "^[l]+[a-zA-Z]+");
        assertNotNull(tuples);
        assertEquals(tuples.size(), 3);

        boolean foundLat= false;
        boolean foundLon = false;
        boolean foundLoc = false;
        try {
            for(StringValueTuple tuple : tuples) {
                if(CellReflector.getLabelAsString(tuple).equals("lat"))
                    foundLat = true;
                if(CellReflector.getLabelAsString(tuple).equals("lon"))
                    foundLon = true;
                if(CellReflector.getLabelAsString(tuple).equals("loc"))
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
            double lat = extractorSingle.getDoubleSingleValue(row1TupleBag.getInternalList(), "lat");
            assertEquals(lat, 11.22);
        } catch (NoSuchElementException e) {
            fail("failed to find lat");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void exactInt() {
        try {
            int loc = extractorSingle.getIntSingleValue(row1TupleBag.getInternalList(), "loc");
            assertEquals(loc, 21911);
        } catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void exactLong() {
        try {
            long loc = extractorSingle.getLongSingleValue(row1TupleBag.getInternalList(), "loc");
            assertEquals(loc, 21911l);
        } catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void exactBytes() {
        try {
            byte[] loc = extractorSingle.getByteSingleValue(row1TupleBag.getInternalList(), "loc");
            assertEquals(loc, "21911".getBytes());
        } catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void stringList() {
        try {
            List<String> items = extractorSingle.getStringList(row1TupleBag.getInternalList(), "event");
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
        }  catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void intList() {
        try {
            TupleBag<IntValueTuple> tupleBag = new TupleBag<IntValueTuple>("fake4id");
            IntValueTuple tuple = new IntValueTuple("edge", 191);
            IntValueTuple tuple2 = new IntValueTuple("edge", 192);
            tupleBag.addCellTuple(tuple);
            tupleBag.addCellTuple(tuple2);

            List<Integer> items = extractorSingle.getIntList(tupleBag.getInternalList(), "edge");
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

        }  catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void longList() {
        try {
            TupleBag<LongValueTuple> tupleBag = new TupleBag<LongValueTuple>("fake4id");
            LongValueTuple tuple = new LongValueTuple("event", 234211213l);
            LongValueTuple tuple2 = new LongValueTuple("event", 234111200l);
            tupleBag.addCellTuple(tuple);
            tupleBag.addCellTuple(tuple2);

            List<Long> items = extractorSingle.getLongList(tupleBag.getInternalList(), "event");
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
        }  catch (NoSuchElementException e) {
            fail("failed to find loc");
        } catch (IllegalArgumentException e){
            fail("bad argument", e);
        }
    }

    @Test
    public void castToLongError() {
        String msg = "row2 contains an event with an invalid long value, and should have thrown an error";
        try {
            extractorSingle.getLongSingleValue(row2TupleBag.getInternalList(), "trylong");
            fail(msg);
        }  catch (NoSuchElementException e) {
            fail(msg);
        } catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("could not be cast to long"));
        }
    }

    @Test
    public void castToDouble() {
        String msg = "row2 contains a lon with an invalid double value, and should have thrown an error";
        try {
            extractorSingle.getDoubleSingleValue(row2TupleBag.getInternalList(), "trydouble");
            fail(msg);
        }  catch (NoSuchElementException e) {
            fail(msg);
        } catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("could not be cast to double"));
        }
    }


}
