package tuple;

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
        label = CellReflector.getLabelAsString(tuple1);
        assertNotNull(label);
        assertEquals(label, "l");
    }

    @Test
    public void valueStr() {
        String value = null;
        value = CellReflector.getValueAsString(tuple1);
        assertNotNull(value);
        assertEquals(value, "v");
    }

    @Test
    public void aux(){
        long auxVal;
        auxVal = CellReflector.getAuxiliaryValue(Long.class, tuple1, "ts");
        assertEquals(auxVal, 0l);

        auxVal = CellReflector.getAuxiliaryValue(Long.class, tupleWithAux, "ts");
        assertEquals(auxVal, 111l);

        CellWithConflictingAuxFields auxCell = new CellWithConflictingAuxFields("l", "v");
        String auxValStr = CellReflector.getAuxiliaryValue(String.class, auxCell, "aux2");
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
        value = CellReflector.getValueAsBytes(tuple);
        assertNotNull(value);
        assertEquals(value, "v".getBytes());
    }

    @Test
    public void valueLongAndInt(){
        IntValueTuple tuple = new IntValueTuple("l", 1);
        int value = 0;
        value = CellReflector.getValueAsInt(tuple);
        assertEquals(value, 1);

        LongValueTuple tuple2 = new LongValueTuple("l", 1l);
        assertNotNull(tuple2);
        long valueLong = 0;
        valueLong = CellReflector.getValueAsLong(tuple2);
        assertNotNull(valueLong);
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
            CellWithCustomClassValue.ValueMockClass ts = CellReflector.getAuxiliaryValue(CellWithCustomClassValue.ValueMockClass.class, tuple, "ts");
            fail("Return should throw class cast exception for mismatch types");
        }  catch (RuntimeException e){
            assertEquals(e.getCause().getClass(), ClassCastException.class);
            assertTrue(e.getMessage().contains("Unable to cast to parameterized type"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void nullAuxValue() {
        try {
            CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
            String aux3 = CellReflector.getAuxiliaryValue(String.class, tuple, "aux3");
            assertNull(aux3);
        }  catch (Exception e){
            fail();
        }
    }

    @Test
    public void nullDataAtValueOrLabel() {
        CellTuple tuple = new CellTuple("l", null);
        String value = CellReflector.getValueAsString(tuple);
        assertNull(value);

        tuple = new CellTuple(null, "v");
        String label= CellReflector.getLabelAsString(tuple);
        assertNull(label);

        byte[] v = null;
        CellWithByteValueTuple tuple2 = new CellWithByteValueTuple("l", v);
        byte[] byteValue = CellReflector.getValueAsBytes(tuple2);
        assertNull(byteValue);
    }

    @Test
    public void conflictingAuxNames() {
        CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
        try {
            CellReflector.getAuxiliaryValue(String.class, tuple, "aux1");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Too many auxiliary fields with matching name"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void lookingForNonexistantAux(){
        CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
        try {
            CellReflector.getAuxiliaryValue(String.class, tuple, "aux4");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("No field matching given annotation name "));
        } catch (Exception e){
            fail();
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
