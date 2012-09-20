package cellmate.extractor;

import cellmate.cell.*;
import com.google.common.annotations.Beta;
import com.google.common.primitives.Primitives;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

/**
 *  Useful static methods to inspect Class instances that have been annotated with
 * {@link cellmate.cell.Cell} and read their contents.</br></br>
 *
 * This requires the class have annotated {@link cellmate.cell.Cell} and
 *  {@link cellmate.cell.Label} and {@link cellmate.cell.Value}</br></br>
 *
 *  The auxiliary methods require the class has one or more fields annotated
 *  with {@link cellmate.cell.CellAuxilaryField}</br>
 *
 */
@Beta
public final class CellReflector {

    private CellReflector(){}

    /**
     *  Looks at any arbitrary cell class and returns the String representation of the label, or
     *  throws an error if malformed, missing annotation, or null.
     *
     * @param obj cell to read the label.
     * @return String label
     * @throws CellExtractorException if any parsing error occurs while reading the field.
     */
    public static String getLabelAsString(Object obj)
        throws CellExtractorException{
        Field field = getLabelField(obj);
        return asString(field, obj);
    }

    /**
     * Looks at any arbitrary cell class and expects to return a String value.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type String.
     * 3) The obj has the Value annotation on a field type that cannot be cast to String.
     * 4) Value is null.
     *
     * @param obj cell to read String value
     * @return String value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static String getValueAsString(Object obj)
        throws CellExtractorException{
        Field field  = getValueField(obj);
        return asString(field, obj);
    }
    /**
     * Looks at any arbitrary cell class and expects to return a double value.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type double.
     * 3) The obj has the Value annotation on a field type that cannot be cast to double.
     * 4) Value is null.
     *
     * @param obj cell to read double value
     * @return double value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static Double getValueAsDouble(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asDouble(field, obj);
    }

    /**
     * Looks at any arbitrary cell class and expects to return a int value.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type int.
     * 3) The obj has the Value annotation on a field type that cannot be cast to int.
     * 4) Value is null.
     *
     * @param obj cell to read int value
     * @return int value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static int getValueAsInt(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asInt(field, obj);
    }

    /**
     * Looks at any arbitrary cell class and expects to return a byte[] value.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type byte[].
     * 3) The obj has the Value annotation on a field type that cannot be cast to String.
     * 4) Value is null.
     *
     * @param obj cell to read byte[] value
     * @return byte[] value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static byte[] getValueAsBytes(Object obj)
            throws CellExtractorException {
        Field field = getValueField(obj);
        try {
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res == null)
                throw new CellExtractorException("Cell value is null", ErrorType.NULL_FIELD);
            return (byte[])res;
        } catch (IllegalAccessException e) {
            throw new CellExtractorException(e, ErrorType.ILLEGAL_ACCESS);
        } catch (ClassCastException e) {
            throw new CellExtractorException("Unable to cast field value as instance of " + byte[].class.getName() +
                    ". Found field class " + field.getType().getName(), e, ErrorType.CLASS_CAST);
        }
    }

    /**
     * Looks at any arbitrary cell class and expects to return a long value.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type long.
     * 3) The obj has the Value annotation on a field type that cannot be cast to long.
     * 4) Value is null.
     *
     * @param obj cell to read long value
     * @return long value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static long getValueAsLong(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asLong(field, obj);
    }

    /**
     *
     * Looks at any arbitrary cell class and expects to return a value of type T.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the Value annotation on a field of type T.
     * 3) The obj has the Value annotation on a field type that cannot be cast to T.
     * 4) Value is null.
     *
     * @param obj cell to read String value
     * @param <T> Value class type.
     * @return T value
     * @throws CellExtractorException if ClassCastException, missing annotations or null value.
     */
    public static <T> T getValueAsInstance(Class<T> clazz, Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asInstance(clazz, field, obj);
    }

    /**
     *
     * Looks at any arbitrary cell class and expects to find a value for some field
     * annotated with CellAuxiliaryType with a matching name attribute.
     *
     * @param clazz Auxiliary field class of type T
     * @param obj  cell instance
     * @param name auxiliary name attribute to find.
     * @param <T>  auxiliary field type.
     * @return Auxiliary value instance for the given cell.
     * @throws CellExtractorException if ClassCastException, missing CellAuxiliaryAnnotation, or null value.
     */
    public static <T> T getAuxiliaryValue(Class<T> clazz, Object obj, String name)
            throws CellExtractorException {
        Field field = getNamedAuxiliaryField(obj, name);
        return asInstance(clazz, field, obj);
    }

    /**
     * Check to see if the given cell object has an CellAuxiliaryField with
     * the given name.
     *
     * @param obj cell to check
     * @param name  name to find.
     * @return  true or false if the cell has the auxiliary name.
     * @throws CellExtractorException if ClassCastException, missing CellAuxiliaryAnnotation, or null value.
     */
    public static boolean hasNamedAuxiliaryField(Object obj, String name)
        throws CellExtractorException{
        return (getNamedAuxiliaryField(obj, name) != null);
    }

    /**
     *
     * Retrieve the column family value from any arbitrary cell with the ColumnFamily annotation.
     *
     * The class will throw a CellExtractionException if the object if any of the following conditions occur
     * 1) obj is missing the Cell annotation
     * 2) obj is missing the ColumnFamily annotation on a field of type T.
     * 3) The obj has the Value annotation on a field type that cannot be cast to T.
     * 4) Value is null.
     *
     * @param cell
     * @return  String colfam
     * @throws CellExtractorException if missing ColumnFamily annotation or the field is null.
     */
    public static String getColFam(Object cell)
            throws CellExtractorException{
        checkCellPresent(cell);
        if(hasColFamField(cell)) {
            Field field = getSingleMatchingField(cell, ColumnFamily.class);
            field.setAccessible(true);
            try {
                Object res = field.get(cell);
                if(res == null){
                    throw new CellExtractorException("Column family value is null", ErrorType.NULL_FIELD);
                }
                return String.class.cast(res);
            } catch (IllegalAccessException e){
                throw new RuntimeException(e);
            }
        } else {
            throw new CellExtractorException("no column family annotated for given cell type: "
                    + cell.getClass().getName(), ErrorType.MISSING_COLFAM_ON_WRITE);
        }
    }

    /**
     *
     * Check to see if a given object has a field annotated with ColumnFamily.
     *
     * See {@link cellmate.cell.ColumnFamily}
     *
     * @param cell to check
     * @return true/false if the ColumnFamily annotation is present.
     * @throws CellExtractorException
     */
    public static boolean hasColFamField(Object cell)
        throws CellExtractorException{
        checkCellPresent(cell);
        Field[] fields = cell.getClass().getDeclaredFields();
        for(Field field: fields){
            if(field.isAnnotationPresent(ColumnFamily.class)){
                return true;
            }
        }
        return false;
    }

    /**
     * Produce a byte array if the provided class contains
     * an Annotated {@link cellmate.cell.Value} and that value can
     * be treated as either a String, long, double, int, or byte[].
     *
     * @param cell to read field value as bytes
     * @return  byte contents of the value.
     * @throws CellExtractorException if the Value type does not match the five main fields,
     *                              an illegal argument is found, or the value is null.
     *
     */
    public static byte[] getValueBytesIfPrimative(Object cell)
        throws CellExtractorException {
        checkCellPresent(cell);
        Field field = getValueField(cell);
        field.setAccessible(true);
        byte[] valueBytes = new byte[8];
        try {
            Type type =  Primitives.unwrap(field.getType());
            if(type.equals(int.class)){
                int value = field.getInt(cell);
                valueBytes = new byte[4];
                ByteBuffer.wrap(valueBytes).putInt(value);
            } else if (type.equals(double.class)) {
                double value = field.getDouble(cell);
                ByteBuffer.wrap(valueBytes).putDouble(value);
            } else if (type.equals(String.class)){
                String value = String.class.cast(field.get(cell));
                if(value != null){
                    valueBytes = value.getBytes();
                } else {
                    throw new CellExtractorException("null value for string", ErrorType.NULL_FIELD);
                }
            } else if (type.equals(byte[].class)){
                 valueBytes = byte[].class.cast(field.get(cell));
            } else if (type.equals(long.class)) {
                long value = field.getLong(cell);
                ByteBuffer.wrap(valueBytes).putLong(value);
            } else {
                throw new CellExtractorException("unsupported type (int, double, long, String, byte[])",
                        ErrorType.UNSUPPORTED_TYPE);
            }
            return valueBytes;
        } catch (IllegalAccessException e) {
            throw new CellExtractorException(e, ErrorType.UNSUPPORTED_TYPE);
        }
    }

    /**
     *
     * Get the class instance representing the annotated
     * {@link cellmate.cell.Value} field in the supplied cell instance.
     *
     * @param obj cell instance to get value type
     * @return  Class of value type.
     * @throws CellExtractorException if obj is not annoated as {@link cellmate.cell.Cell}
     */
    public static Class<?> getValueType(Object obj)
            throws CellExtractorException {
        checkCellPresent(obj);
        Field field = getSingleMatchingField(obj, Value.class);
        return field.getType();
    }

    private static Field getNamedAuxiliaryField(Object obj, String name)
        throws CellExtractorException{

        checkCellPresent(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        Field found = null;
        int foundCount = 0;
        for(Field field : fields) {
            if(field.isAnnotationPresent(CellAuxilaryField.class)){
                CellAuxilaryField ans = field.getAnnotation(CellAuxilaryField.class);
                if(ans.name().equals(name)){
                    found = field;
                    foundCount++;
                }
            }
        }
        if(found == null)
            sendFieldError("Did you forget to name the auxilary field?. " +
                    "No field matching cell auxiliary fields with given name: " +
                    name + ". ", CellAuxilaryField.class, ErrorType.MISSING_FIELD);
        if(foundCount > 1)
            sendFieldError("Too many auxiliary fields with matching name: " +
                    name + ". ", CellAuxilaryField.class, ErrorType.TOO_MANY_FIELDS);
        return found;
    }

    private static <T> T asInstance(Class<T> type, Field field, Object obj)
            throws CellExtractorException {
        try {
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res == null)
                throw new CellExtractorException("null value for type: " + type.getName(), ErrorType.NULL_FIELD);
            return type.cast(res);
        }  catch (IllegalAccessException e) {
            throw new CellExtractorException(e, ErrorType.ILLEGAL_ACCESS);
        }  catch(ClassCastException e){
            throw new CellExtractorException("Unable to cast field value as instance of " + type.getName() +
                    ". Found field class " + field.getType().getName(), e, ErrorType.CLASS_CAST);
        }
    }

    private static String asString(Field field, Object obj)
        throws CellExtractorException{
        return (String)getAs(String.class, field, obj);
    }

    private static int asInt(Field field, Object obj)
        throws CellExtractorException{
        return (Integer)getAs(Integer.class, field, obj);
    }

    private static long asLong(Field field, Object obj)
        throws CellExtractorException{
        return (Long)getAs(Long.class, field, obj);
    }

    private static double asDouble(Field field, Object obj)
            throws CellExtractorException{
        return (Double)getAs(Double.class, field, obj);
    }

    private static Object getAs(Class type, Field field, Object obj)
            throws CellExtractorException {
        try{
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res == null)
                throw new CellExtractorException("Cell value is null", ErrorType.NULL_FIELD);
            return type.cast(res);
        } catch (ClassCastException e) {
            throw new CellExtractorException("Unable to cast field value as instance of " + type.getName() +
                    ". Found field class of "
                    + field.getType().getName(), e, ErrorType.CLASS_CAST);
        } catch (IllegalAccessException e) {
            throw new CellExtractorException(e, ErrorType.ILLEGAL_ACCESS);
        }
    }

    /**
     * Get the label field for a given cell, if present.
     *
     * @param obj cell to get label field
     * @return Field annotated with {@link cellmate.cell.Label}
     * @throws CellExtractorException if obj is not annotated as {@link cellmate.cell.Cell}
     *      or multiple fields are annoted with {@link cellmate.cell.Label}
     */
    public static Field getLabelField(Object obj) throws CellExtractorException {
        Class<?> clz = obj.getClass();
        return getSingleMatchingField(obj, Label.class);
    }

    /**
     * Get the value field for a given cell, if present.
     *
     * @param obj cell to get value field
     * @return Field annotated with {@link cellmate.cell.Value}
     * @throws CellExtractorException if obj is not annotated as {@link cellmate.cell.Cell}
     *      or multiple fields are annoted with {@link cellmate.cell.Value}
     */
    public static Field getValueField(Object obj) throws CellExtractorException {
        return getSingleMatchingField(obj, Value.class);
    }

    private static Field getSingleMatchingField(Object obj,
                                                Class<? extends Annotation> clazz)
            throws CellExtractorException
    {

        checkCellPresent(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        Field found = null;
        int foundCount = 0;
        for(Field field : fields) {
            if(field.isAnnotationPresent(clazz)){
                found = field;
                foundCount++;
            }
        }
        if(found == null) {
            sendFieldError("No field found in cell class with annotation: ",
                    clazz, ErrorType.MISSING_FIELD);
        }
        if(foundCount > 1) {
            sendFieldError("More than one field found with annotation: ",
                    clazz, ErrorType.TOO_MANY_FIELDS);
        }
        return found;
    }

    private static void checkCellPresent(Object obj) throws CellExtractorException {
        if(!obj.getClass().isAnnotationPresent(Cell.class))
            throw new CellExtractorException("Class is not annotated as a cell",
                    ErrorType.MISSING_ANNOTATION);
    }

    private static void sendFieldError(String msg,
                                       Class<? extends Annotation> clazz,
                                       ErrorType errorType)
            throws CellExtractorException {
        throw new CellExtractorException(msg + clazz.getName(), errorType);
    }
}
