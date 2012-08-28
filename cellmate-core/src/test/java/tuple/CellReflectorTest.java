package tuple;

import cellmate.exception.NullDataForLabelValueException;
import cellmate.tuple.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:35 PM
 */
public class CellReflectorTest {

    CellTuple tuple1;
    CellTuple tupleWithAux;

    @BeforeClass
    public void setup(){
        tuple1 = new CellTuple("l", "v");
        tupleWithAux = new CellTuple("l", "v", 111l);
    }

    @Test
    public void label(){
        String label = null;
        try {
            label = CellReflector.getLabelAsString(tuple1);
        } catch (NullDataForLabelValueException e) {
            fail();
        }
        assertEquals(label, "l");
    }

    @Test
    public void valueStr() {
        String value = null;
        try {
            value = CellReflector.getValueAsString(tuple1);
        } catch (NullDataForLabelValueException e) {
            fail();
        }
        assertEquals(value, "v");
    }

    @Test
    public void aux(){
        Long auxVal = CellReflector.getAuxiliaryValue(tuple1, "ts");
        assertEquals(auxVal, new Long(0l));

        auxVal = CellReflector.getAuxiliaryValue(tupleWithAux, "ts");
        assertEquals(auxVal, new Long(111l));

        CellWithConflictingAuxFields auxCell = new CellWithConflictingAuxFields("l", "v");
        String auxValStr = CellReflector.getAuxiliaryValue(auxCell, "aux2");
        assertEquals(auxValStr, "blah");

    }

    @Test
    public void invalidCast() {
        try {
            CellReflector.getValueAsLong(tuple1);
            fail("value of tuple should not have cast to long");
        } catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail();
        }

        try {
            CellReflector.getValueAsInt(tuple1);
            fail("value of tuple should not have cast to int");
        } catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void checkCellPresent() {
        MissingCellAnnTuple tuple = new MissingCellAnnTuple("l", "v");
        try {
            CellReflector.getLabelAsString(tuple);
            fail("cell is missing annotation");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Class is not annotated as a cell"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void valueBytes() {
        CellWithByteValueTuple tuple = new CellWithByteValueTuple("l", "v".getBytes());
        byte [] value = new byte[0];
        try {
            value = CellReflector.getValueAsBytes(tuple);
        } catch (NullDataForLabelValueException e) {
            fail();
        }
        assertEquals(value, "v".getBytes());
    }

    @Test
    public void valueLongAndInt(){
        IntValueTuple tuple = new IntValueTuple("l", 1);
        int value = 0;
        try {
            value = CellReflector.getValueAsInt(tuple);
        } catch (NullDataForLabelValueException e) {
            fail();
        }
        assertEquals(value, 1);

        LongValueTuple tuple2 = new LongValueTuple("l", 1l);
        long valueLong = 0;
        try {
            valueLong = CellReflector.getValueAsLong(tuple2);
        } catch (NullDataForLabelValueException e) {
            fail();
        }
        assertEquals(valueLong, 1l);
    }

    @Test
    public void missingLabelAndValue() {
        try {
            MissingLabelAndValueTuple tuple = new MissingLabelAndValueTuple("l", "v");
            CellReflector.getLabelAsString(tuple);
            fail("no label annotation was applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("No field found in cell class"));
        } catch (Exception e){
            fail();
        }

        try {
            MissingLabelAndValueTuple tuple = new MissingLabelAndValueTuple("l", "v");
            CellReflector.getValueAsString(tuple);
            fail("no value annotation was applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("No field found in cell class"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void tooManyLabelsValues() {
        try {
            TooManyLabelValueTuple tuple = new TooManyLabelValueTuple("l", "v".getBytes());
            CellReflector.getLabelAsString(tuple);
            fail("should throw error. multiple labels were applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("More than one field found with annotation"));
        } catch (Exception e){
            fail();
        }

        try {
            TooManyLabelValueTuple tuple = new TooManyLabelValueTuple("l", "v".getBytes());
            CellReflector.getValueAsString(tuple);
            fail("should throw error. multiple values were applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("More than one field found with annotation"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void valueInstance () {
        CellWithCustomClassValue tuple = new CellWithCustomClassValue("l", "v");
        CellWithCustomClassValue.ValueMockClass value =
                CellReflector.getValueAsInstance(CellWithCustomClassValue.ValueMockClass.class, tuple);
        assertNotNull(value);
        assertEquals(value.getHolder(), "v");
    }

    @Test
    public void invalidValueInstanceCast() {
        try {
            CellWithCustomClassValue tuple = new CellWithCustomClassValue("l", "v");
            String value =  CellReflector.getValueAsInstance(String.class, tuple);
            fail("should not cast correctly to string");
        } catch (RuntimeException e){
            assertEquals(e.getCause().getClass(), ClassCastException.class);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void invalidCellAuxInstanceCast() {
        try {
            CellTuple tuple = new CellTuple("l", "v", 111l);
            CellWithCustomClassValue.ValueMockClass ts = CellReflector.getAuxiliaryValue(tuple, "ts");
            fail("Return should throw class cast exception for mismatch types");
        }  catch (ClassCastException e){
            assertTrue(true);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void nullAuxValue() {
       try {
            CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
            String aux3 = CellReflector.getAuxiliaryValue(tuple, "aux3");
            fail("aux3 is null");
        }  catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Found auxiliary field (aux3) for instance, but value was null"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void nullDataAtValueOrLabel() {
        try {
            CellTuple tuple = new CellTuple("l", null);
            CellReflector.getValueAsString(tuple);
            fail("null data");
        } catch (NullDataForLabelValueException e) {
            assertTrue(e.getMessage().contains("found null for data/value"));
        }

        try {
            CellTuple tuple = new CellTuple(null, "v");
            CellReflector.getLabelAsString(tuple);
            fail("null data");
        } catch (NullDataForLabelValueException e) {
            assertTrue(e.getMessage().contains("found null for data/value"));
        }

        try {
            byte[] v = null;
            CellWithByteValueTuple tuple = new CellWithByteValueTuple("l", v);
            CellReflector.getValueAsBytes(tuple);
            fail("null data");
        } catch (NullDataForLabelValueException e) {
            assertTrue(e.getMessage().contains("found null for data/value"));
        }
    }

    @Test
    public void conflictingAuxNames() {
        CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
        try {
            CellReflector.getAuxiliaryValue(tuple, "aux1");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Too many auxiliary fields with matching name"));
        }
    }

    @Test
    public void lookingForNonexistantAux(){
        CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
        try {
            CellReflector.getAuxiliaryValue(tuple, "aux4");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("No field matching given annotation name "));
        }
    }


    @Cell
    public class CellWithByteValueTuple {
        @Label
        private String label;

        @Value
        private byte[] value;

        public CellWithByteValueTuple(String label, byte[] value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class TooManyLabelValueTuple {
        @Label
        private String label;

        @Label
        private String label2;

        @Value
        private byte[] value;

        @Value
        private byte[] value2;

        public TooManyLabelValueTuple(String label, byte[] value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class CellWithConflictingAuxFields {

        @Label
        private String label;
        @Value
        private String value;

        @CellAuxilaryField(name="aux1")
        private String aux1;

        @CellAuxilaryField(name="aux1")
        private String aux4;

        @CellAuxilaryField(name="aux3")
        private String aux3;

        @CellAuxilaryField(name="aux2")
        private String aux2;

        public CellWithConflictingAuxFields(String label, String value) {
            this.label = label;
            this.value = value;
            this.aux1 = "";
            this.aux4 = "";
            this.aux2 = "blah";
            this.aux3 = null;
        }
    }

    @Cell
    public class CellWithCustomClassValue {
        @Label
        private String label;

        @Value
        private ValueMockClass value;

        public CellWithCustomClassValue(String label, String value) {
            this.label = label;
            this.value = new ValueMockClass(value);
        }

        private class ValueMockClass {

            private String holder;

            private ValueMockClass(String holder) {
                this.holder = holder;
            }

            public String getHolder() {
                return holder;
            }
        }

        public ValueMockClass getValue(){
            return value;
        }
    }

    public class MissingCellAnnTuple {

        @Label
        private String label;
        @Value
        private String value;

        public MissingCellAnnTuple(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class MissingLabelAndValueTuple {

        private String label;
        private String value;

        public MissingLabelAndValueTuple(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

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
}
