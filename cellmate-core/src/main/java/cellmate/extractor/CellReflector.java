package cellmate.extractor;

import cellmate.cell.*;
import com.google.common.annotations.Beta;
import com.google.common.primitives.Primitives;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 12:44 PM
 */
@Beta
public final class CellReflector {

    private CellReflector(){}

    public static String getLabelAsString(Object obj)
        throws CellExtractorException{
        Field field = getLabelField(obj);
        return asString(field, obj);
    }

    public static String getValueAsString(Object obj)
        throws CellExtractorException{
        Field field  = getValueField(obj);
        return asString(field, obj);
    }

    public static Double getValueAsDouble(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asDouble(field, obj);
    }

    public static int getValueAsInt(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asInt(field, obj);
    }

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

    public static long getValueAsLong(Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asLong(field, obj);
    }

    public static <T> T getValueAsInstance(Class<T> clazz, Object obj)
        throws CellExtractorException{
        Field field = getValueField(obj);
        return asInstance(clazz, field, obj);
    }

    public static <T> T getAuxiliaryValue(Class<T> clazz, Object obj, String name)
            throws CellExtractorException {
        Field field = getNamedAuxiliaryField(obj, name);
        return asInstance(clazz, field, obj);
    }

    public static boolean hasNamedAuxiliaryField(Object obj, String name)
        throws CellExtractorException{
        return (getNamedAuxiliaryField(obj, name) != null);
    }

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

    public static Field getLabelField(Object obj) throws CellExtractorException {
        Class<?> clz = obj.getClass();
        return getSingleMatchingField(obj, Label.class);
    }

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
