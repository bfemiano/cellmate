package cellmate.extractor;

import cellmate.cell.*;
import cellmate.extractor.CellReflector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

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
        try {
            assertTrue(CellReflector.hasColFamField(cell));
            assertEquals(CellReflector.getColFam(cell), "cf");
        } catch (CellExtractorException e) {
            fail("did not find col fam annotation or null column fam");
        }

        IntValueCell intCell = new IntValueCell("l", 1);
        try {
            assertFalse(CellReflector.hasColFamField(intCell));
            CellReflector.getColFam(intCell);
            fail("should not find field");
        } catch (CellExtractorException e) {
            assertTrue(e.getMessage().contains("no column family annotated for given cell type:"));
            assertEquals(e.getType(), ErrorType.MISSING_COLFAM_ON_WRITE);
        }

        cell = new StringValueCell("l", "v", 1l);
        try {
            CellReflector.getColFam(cell);
            fail("should throw CellExtractionException even when null");
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
            assertTrue(e.getMessage().contains("Column family value is null"));
        }
    }

    @Test
    public void hasNamedAuxValue() {
        try {
            assertTrue(CellReflector.hasNamedAuxiliaryField(cellWithAux, "ts"));
        } catch (CellExtractorException e) {
            fail("cell extraction error",e);
        }
    }

    @Test
    public void label(){
        String label = null;
        try {
            label = CellReflector.getLabelAsString(cell1);
        } catch (CellExtractorException e) {
            fail("cell extraction error",e);
        }
        assertEquals(label, "l");
    }

    @Test
    public void valueStr() {
        String value = null;
        try {
            value = CellReflector.getValueAsString(cell1);
        } catch (CellExtractorException e) {
            fail("cell extraction error");
        }
        assertEquals(value, "v");
    }

    @Test
    public void getValueBytes() {
        StringValueCell strCell = new StringValueCell("l", "v");
        StringValueCell nullStrCell = new StringValueCell("l", null);
        IntValueCell    intCell = new IntValueCell("l", 1);
        LongValueCell lonCell = new LongValueCell("l", 2l);
        DoubleValueCell dbCell = new DoubleValueCell("l", 11.11);
        ByteValueCell byteCell = new ByteValueCell("l", "v".getBytes());

        try {
            byte[] strBytes = CellReflector.getValueBytesIfPrimative(strCell);
            assertEquals(strBytes, "v".getBytes());

            byte[] lonBytes = CellReflector.getValueBytesIfPrimative(lonCell);
            byte[] testLongBytes = new byte[8];
            ByteBuffer.wrap(testLongBytes).putLong(2l);
            assertEquals(lonBytes, testLongBytes);

            byte[] intBytes = CellReflector.getValueBytesIfPrimative(intCell);
            byte[] testIntBytes = new byte[4];
            ByteBuffer.wrap(testIntBytes).putInt(1);
            assertEquals(intBytes, testIntBytes);

            byte[] doubleBytes = CellReflector.getValueBytesIfPrimative(dbCell);
            byte[] testDoubleBytes = new byte[8];
            ByteBuffer.wrap(testDoubleBytes).putDouble(11.11);
            assertEquals(doubleBytes, testDoubleBytes);

            byte[] bytesValue = CellReflector.getValueBytesIfPrimative(byteCell);
            byte[] testByteArray = "v".getBytes();
            assertEquals(bytesValue, testByteArray);
        } catch (CellExtractorException e){
            fail("cell extraction error", e);
        }


        try {
            CellReflector.getValueBytesIfPrimative(nullStrCell);
        }  catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
            assertTrue(e.getMessage().contains("null value"));
        }   catch (Exception e){
            fail();
        }

        try {
            CellWithCustomClassValue cell = new CellWithCustomClassValue("l","v");
            CellReflector.getValueBytesIfPrimative(cell);
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.UNSUPPORTED_TYPE);
            assertTrue(e.getMessage().contains("unsupported type"));
        }  catch (Exception e){
            fail();
        }


    }

    @Test
    public void aux(){
        long auxVal;

        try {
            auxVal = CellReflector.getAuxiliaryValue(Long.class, cell1, "ts");
            assertEquals(auxVal, 0l);

            auxVal = CellReflector.getAuxiliaryValue(Long.class, cellWithAux, "ts");
            assertEquals(auxVal, 111l);

            CellWithConflictingAuxFields auxCell = new CellWithConflictingAuxFields("l", "v");
            String auxValStr = CellReflector.getAuxiliaryValue(String.class, auxCell, "aux2");
            assertEquals(auxValStr, "blah");
        } catch (CellExtractorException e){
            fail("cell extraction error",e);
        }

    }

    @Test
    public void invalidCast() {
        try {
            CellReflector.getValueAsLong(cell1);
            fail("value of cell should not have cast to long");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail("unknown error",e);
        }

        try {
            CellReflector.getValueAsInt(cell1);
            fail("value of cell should not have cast to int");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
            assertTrue(e.getCause().getClass().equals(ClassCastException.class));
        } catch (Exception e){
            fail("unknown error",e);
        }
    }

    @Test
    public void checkCellPresent() {
        MissingCellAnnotationCell tupleCellAnnotation = new MissingCellAnnotationCell("l", "v");
        try {
            CellReflector.getLabelAsString(tupleCellAnnotation);
            fail("cell is missing annotation");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.MISSING_ANNOTATION);
            assertTrue(e.getMessage().contains("Class is not annotated as a cell"));
        } catch (Exception e){
            fail("unknown error",e);
        }
    }

    @Test
    public void valueBytes() {
        CellWithByteValue cell = new CellWithByteValue("l", "v".getBytes());
        byte [] value = {};
        try {
            value = CellReflector.getValueAsBytes(cell);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertNotNull(value);
        assertEquals(value, "v".getBytes());
    }

    @Test
    public void valueLongAndInt(){
        IntValueCell cell = new IntValueCell("l", 1);
        int value = 0;
        try {
            value = CellReflector.getValueAsInt(cell);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertEquals(value, 1);

        LongValueCell tuple2 = new LongValueCell("l", 1l);
        assertNotNull(tuple2);
        long valueLong = 0;
        try {
            valueLong = CellReflector.getValueAsLong(tuple2);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertNotNull(valueLong);
        assertEquals(valueLong, 1l);
    }

    @Test
    public void valueDouble(){
        DoubleValueCell cell = new DoubleValueCell("l", 22.22d);
        double value = 0;
        try {
            value = CellReflector.getValueAsDouble(cell);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertEquals(value, 22.22d);
    }

    @Test
    public void missingLabelAndValue() {
        try {
            MissingLabelAndValueCell cell = new MissingLabelAndValueCell("l", "v");
            CellReflector.getLabelAsString(cell);
            fail("no label annotation was applied");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.MISSING_FIELD);
            assertTrue(e.getMessage().contains("No field found in cell class"));
        } catch (Exception e){
            fail();
        }

        try {
            MissingLabelAndValueCell cell = new MissingLabelAndValueCell("l", "v");
            CellReflector.getValueAsString(cell);
            fail("no value annotation was applied");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.MISSING_FIELD);
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
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.TOO_MANY_FIELDS);
            assertTrue(e.getMessage().contains("More than one field found with annotation"));
        } catch (Exception e){
            fail();
        }

        try {
            TooManyLabelValueCell cell = new TooManyLabelValueCell("l", "v".getBytes());
            CellReflector.getValueAsString(cell);
            fail("should throw error. multiple values were applied");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.TOO_MANY_FIELDS);
            assertTrue(e.getMessage().contains("More than one field found with annotation"));
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void valueInstance () {
        CellWithCustomClassValue tuple = new CellWithCustomClassValue("l", "v");
        CellWithCustomClassValue.ValueMockClass value =
                null;
        try {
            value = CellReflector.getValueAsInstance(CellWithCustomClassValue.ValueMockClass.class, tuple);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertNotNull(value);
        assertEquals(value.getHolder(), "v");
    }

    @Test
    public void invalidValueInstanceCast() {
        try {
            CellWithCustomClassValue tuple = new CellWithCustomClassValue("l", "v");
            String value =  CellReflector.getValueAsInstance(String.class, tuple);
            fail("should not cast correctly to string");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
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
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
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
        }  catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.CLASS_CAST);
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
            CellReflector.getAuxiliaryValue(String.class, tuple, "aux3");
            fail("aux3 is null. Should have thrown a CellExtractionException");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        } catch (Exception e){
            fail();
        }
    }

    @Test
    public void nullDataAtValueOrLabel() {
        StringValueCell cell;
        try {
            cell = new StringValueCell("l", null);
            String value = CellReflector.getValueAsString(cell);
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }


        cell = new StringValueCell(null, "v");
        String label= null;
        try {
            CellReflector.getLabelAsString(cell);
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }

        byte[] v = null;
        CellWithByteValue a2 = new CellWithByteValue("l", v);
        byte[] byteValue = new byte[0];
        try {
            CellReflector.getValueAsBytes(a2);
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
        }
    }

    @Test
    public void conflictingAuxNames() {
        CellWithConflictingAuxFields tuple = new CellWithConflictingAuxFields("l", "v");
        try {
            CellReflector.getAuxiliaryValue(String.class, tuple, "aux1");
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.TOO_MANY_FIELDS);
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
        } catch (CellExtractorException e){
            assertEquals(e.getType(), ErrorType.MISSING_FIELD);
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
