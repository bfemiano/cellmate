package cellmate.extractor;

import cellmate.tuple.*;
import cellmate.extractor.StringMultiSingleValueCellExtractor;
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

    RowIDTuples<CellTuple> row1Tuples;
    RowIDTuples<CellTuple> row2Tuples;
    private StringMultiSingleValueCellExtractor extractorSingle =
            new StringMultiSingleValueCellExtractor();
    private RowIDTuples<CellTuple> row3Tuples;

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
        row1Tuples = new RowIDTuples<CellTuple>("fakeId1");
        CellTuple tuple1 = new CellTuple("name", "brian");
        CellTuple tuple2 = new CellTuple("event", "234211213");
        CellTuple tuple3 = new CellTuple("event", "234111200");
        CellTuple tuple4 = new CellTuple("edge", "191");
        CellTuple tuple5 = new CellTuple("edge", "192");
        CellTuple tuple6 = new CellTuple("chk_t", "00-33-11");
        CellTuple tuple7 = new CellTuple("ckk_d", "2012-08-22", 238124123l);
        CellTuple tuple8 = new CellTuple("loc", "21911", 238124123l);
        CellTuple tuple9 = new CellTuple("lat", "11.22");
        CellTuple tuple10 = new CellTuple("lon", "33.44");

        row1Tuples.addCellTuple(tuple1);
        row1Tuples.addCellTuple(tuple2);
        row1Tuples.addCellTuple(tuple3);
        row1Tuples.addCellTuple(tuple4);
        row1Tuples.addCellTuple(tuple5);
        row1Tuples.addCellTuple(tuple6);
        row1Tuples.addCellTuple(tuple7);
        row1Tuples.addCellTuple(tuple8);
        row1Tuples.addCellTuple(tuple9);
        row1Tuples.addCellTuple(tuple10);

        row2Tuples = new RowIDTuples<CellTuple>("fakeId2");
        tuple1 = new CellTuple("name", "brian");
        tuple4 = new CellTuple("trylong", "error");
        tuple5 = new CellTuple("edge", "192");
        tuple6 = new CellTuple("edge", "error");
        tuple7 = new CellTuple("chk_t", "00-33-11");
        tuple8 = new CellTuple("chk_t", "00-33-11");
        tuple9 = new CellTuple("chk_d", "2012-08-22", 238124123l);
        tuple10 = new CellTuple("loc", "21911", 238124123l);
        CellTuple tuple11 = new CellTuple("trydouble", "error");
        row2Tuples.addCellTuple(tuple1);
        row2Tuples.addCellTuple(tuple2);
        row2Tuples.addCellTuple(tuple3);
        row2Tuples.addCellTuple(tuple4);
        row2Tuples.addCellTuple(tuple5);
        row2Tuples.addCellTuple(tuple6);
        row2Tuples.addCellTuple(tuple7);
        row2Tuples.addCellTuple(tuple8);
        row2Tuples.addCellTuple(tuple9);
        row2Tuples.addCellTuple(tuple10);
        row2Tuples.addCellTuple(tuple11);

        row3Tuples = new RowIDTuples<CellTuple>("fakeId3");
        row3Tuples.addCellTuple(new CellTuple("event", "e1", 111111l));
        row3Tuples.addCellTuple(new CellTuple("event", "e2", 222222l));
        row3Tuples.addCellTuple(new CellTuple("event", "e3", 333333l));
    }

    @Test
    public void tupleCounts() {
        assertEquals(row1Tuples.getInternalList().size(), 10);
        assertEquals(row2Tuples.getInternalList().size(), 11);
    }

    @Test
    public void testMostRecentTimestamp() {
        CellTuple mostRecent = extractorSingle.getMostRecentTimestamp(row3Tuples.getInternalList());
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
        List<CellTuple> tuples = extractorSingle.matchLabel(row1Tuples.getInternalList(), "name");
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
        List<CellTuple> tuples = extractorSingle.regexMatchLabel(row1Tuples.getInternalList(), "^[l]+[a-zA-Z]+");
        assertNotNull(tuples);
        assertEquals(tuples.size(), 3);

        boolean foundLat= false;
        boolean foundLon = false;
        boolean foundLoc = false;
        try {
            for(CellTuple tuple : tuples) {
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
            double lat = extractorSingle.getDoubleSingleValue(row1Tuples.getInternalList(), "lat");
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
            int loc = extractorSingle.getIntSingleValue(row1Tuples.getInternalList(), "loc");
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
            long loc = extractorSingle.getLongSingleValue(row1Tuples.getInternalList(), "loc");
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
            byte[] loc = extractorSingle.getByteSingleValue(row1Tuples.getInternalList(), "loc");
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
            List<String> items = extractorSingle.getStringList(row1Tuples.getInternalList(), "event");
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
            RowIDTuples<IntValueTuple> tuples = new RowIDTuples<IntValueTuple>("fake4id");
            IntValueTuple tuple = new IntValueTuple("edge", 191);
            IntValueTuple tuple2 = new IntValueTuple("edge", 192);
            tuples.addCellTuple(tuple);
            tuples.addCellTuple(tuple2);

            List<Integer> items = extractorSingle.getIntList(tuples.getInternalList(), "edge");
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
            RowIDTuples<LongValueTuple> tuples = new RowIDTuples<LongValueTuple>("fake4id");
            LongValueTuple tuple = new LongValueTuple("event", 234211213l);
            LongValueTuple tuple2 = new LongValueTuple("event", 234111200l);
            tuples.addCellTuple(tuple);
            tuples.addCellTuple(tuple2);

            List<Long> items = extractorSingle.getLongList(tuples.getInternalList(), "event");
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
            extractorSingle.getLongSingleValue(row2Tuples.getInternalList(), "trylong");
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
            extractorSingle.getDoubleSingleValue(row2Tuples.getInternalList(), "trydouble");
            fail(msg);
        }  catch (NoSuchElementException e) {
            fail(msg);
        } catch (IllegalArgumentException e){
            assertTrue(e.getMessage().contains("could not be cast to double"));
        }
    }


}
