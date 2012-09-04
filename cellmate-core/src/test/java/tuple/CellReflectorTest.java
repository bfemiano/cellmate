package tuple;

import cellmate.cell.*;
import com.sun.jdi.DoubleValue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Type;

import static org.testng.Assert.*;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 2:35 PM
 */
public class CellReflectorTest {

    StringValueCell cell1;
    StringValueCell cellWithAux;

    @BeforeClass
    public void setup(){
        cell1 = new StringValueCell("l", "v");
        cellWithAux = new StringValueCell("l", "v", 111l);
    }

    @Test
    public void colFam() {
        StringValueCell cell = new StringValueCell("l", "v", "cf");
        assertTrue(CellReflector.hasColFamField(cell));
        try {
            assertEquals(CellReflector.getColFam(cell), "cf");
        } catch (NoSuchFieldException e) {
            fail("did not find col fam annotation or null column fam");
        }

        IntValueCell intCell = new IntValueCell("l", 1);
        assertFalse(CellReflector.hasColFamField(intCell));
        try {
            CellReflector.getColFam(intCell);
            fail("should not find field");
        } catch (NoSuchFieldException e) {
            assertTrue(e.getMessage().contains("no column family annotated for given cell type:"));
        }

        cell = new StringValueCell("l", "v", 1l);
        try {
            CellReflector.getColFam(cell);
            fail("should throw NoSuchFieldException even when null");
        } catch (NoSuchFieldException e) {
            assertTrue(e.getMessage().contains("column family for cell is null"));
        }
    }

    @Test
    public void hasNamedAuxValue() {
        assertTrue(CellReflector.hasNamedAuxiliaryField(cellWithAux, "ts"));
    }

    @Test
    public void label(){
        String label = null;
        label = CellReflector.getLabelAsString(cell1);
        assertNotNull(label);
        assertEquals(label, "l");
    }

    @Test
    public void valueStr() {
        String value = null;
        value = CellReflector.getValueAsString(cell1);
        assertNotNull(value);
        assertEquals(value, "v");
    }

    @Test
    public void aux(){
        long auxVal;
        auxVal = CellReflector.getAuxiliaryValue(Long.class, cell1, "ts");
        assertEquals(auxVal, 0l);

        auxVal = CellReflector.getAuxiliaryValue(Long.class, cellWithAux, "ts");
        assertEquals(auxVal, 111l);

        CellWithConflictingAuxFields auxCell = new CellWithConflictingAuxFields("l", "v");
        String auxValStr = CellReflector.getAuxiliaryValue(String.class, auxCell, "aux2");
        assertEquals(auxValStr, "blah");
    }

    @Test
    public void invalidCast() {
        try {
            CellReflector.getValueAsLong(cell1);
            fail("value of cell should not have cast to long");
        } catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail();
        }

        try {
            CellReflector.getValueAsInt(cell1);
            fail("value of cell should not have cast to int");
        } catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void checkCellPresent() {
        MissingCellAnnotationCell tupleCellAnnotation = new MissingCellAnnotationCell("l", "v");
        try {
            CellReflector.getLabelAsString(tupleCellAnnotation);
            fail("cell is missing annotation");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("Class is not annotated as a cell"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void valueBytes() {
        CellWithByteValue cell = new CellWithByteValue("l", "v".getBytes());
        byte [] value = {};
        value = CellReflector.getValueAsBytes(cell);
        assertNotNull(value);
        assertEquals(value, "v".getBytes());
    }

    @Test
    public void valueLongAndInt(){
        IntValueCell cell = new IntValueCell("l", 1);
        int value = 0;
        value = CellReflector.getValueAsInt(cell);
        assertEquals(value, 1);

        LongValueCell tuple2 = new LongValueCell("l", 1l);
        assertNotNull(tuple2);
        long valueLong = 0;
        valueLong = CellReflector.getValueAsLong(tuple2);
        assertNotNull(valueLong);
        assertEquals(valueLong, 1l);
    }

    @Test
    public void valueDouble(){
        DoubleValueCell cell = new DoubleValueCell("l", 22.22d);
        double value = CellReflector.getValueAsDouble(cell);
        assertEquals(value, 22.22d);
    }

    @Test
    public void missingLabelAndValue() {
        try {
            MissingLabelAndValueCell cell = new MissingLabelAndValueCell("l", "v");
            CellReflector.getLabelAsString(cell);
            fail("no label annotation was applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("No field found in cell class"));
        } catch (Exception e){
            fail();
        }

        try {
            MissingLabelAndValueCell cell = new MissingLabelAndValueCell("l", "v");
            CellReflector.getValueAsString(cell);
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
            TooManyLabelValueCell cell = new TooManyLabelValueCell("l", "v".getBytes());
            CellReflector.getLabelAsString(cell);
            fail("should throw error. multiple labels were applied");
        } catch (RuntimeException e){
            assertTrue(e.getMessage().contains("More than one field found with annotation"));
        } catch (Exception e){
            fail();
        }

        try {
            TooManyLabelValueCell cell = new TooManyLabelValueCell("l", "v".getBytes());
            CellReflector.getValueAsString(cell);
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
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
            assertTrue(e.getMessage().contains("Unable to cast field value as instance of "));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void invalidByteValue() {
        try {
            StringValueCell cell = new StringValueCell("l", "v");
            byte[] byteValue = CellReflector.getValueAsBytes(cell);
            fail("cell extractor should have throw error");
        } catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
            assertTrue(e.getMessage().contains("Unable to cast field value as instance of "));
        }
    }

    @Test
    public void getValueType() {
        try {
            IntValueCell cell = new IntValueCell("l", 1);
            Class<?> typeOfCellValue = CellReflector.getValueType(cell);
            assertEquals(typeOfCellValue.getClass(), Integer.class.getClass());
            assertNotEquals(typeOfCellValue, String.class);
            assertNotEquals(typeOfCellValue, Double.class);
        } catch (Exception e){
            fail(e.getMessage(), e);
        }
    }

    @Test
    public void invalidCellAuxInstanceCast() {
        try {
            StringValueCell cell = new StringValueCell("l", "v", 111l);
            CellWithCustomClassValue.ValueMockClass ts = CellReflector.getAuxiliaryValue(CellWithCustomClassValue.ValueMockClass.class, cell, "ts");
            fail("Return should throw class cast exception for mismatch types");
        }  catch (RuntimeException e){
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
            assertTrue(e.getMessage().contains("Unable to cast field value as instance of "));
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
        StringValueCell cell = new StringValueCell("l", null);
        String value = CellReflector.getValueAsString(cell);
        assertNull(value);

        cell = new StringValueCell(null, "v");
        String label= CellReflector.getLabelAsString(cell);
        assertNull(label);

        byte[] v = null;
        CellWithByteValue a2 = new CellWithByteValue("l", v);
        byte[] byteValue = CellReflector.getValueAsBytes(a2);
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
            assertTrue(e.getMessage().contains("No field matching cell auxiliary fields with given name: "));
        } catch (Exception e){
            fail();
        }
    }


    @Cell
    public class CellWithByteValue {
        @Label
        private String label;

        @Value
        private byte[] value;

        public CellWithByteValue(String label, byte[] value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class TooManyLabelValueCell {
        @Label
        private String label;

        @Label
        private String label2;

        @Value
        private byte[] value;

        @Value
        private byte[] value2;

        public TooManyLabelValueCell(String label, byte[] value) {
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

    public class MissingCellAnnotationCell {

        @Label
        private String label;
        @Value
        private String value;

        public MissingCellAnnotationCell(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    @Cell
    public class MissingLabelAndValueCell {

        private String label;
        private String value;

        public MissingLabelAndValueCell(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}
