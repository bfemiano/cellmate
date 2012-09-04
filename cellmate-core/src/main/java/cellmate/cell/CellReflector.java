package cellmate.cell;

import com.google.common.annotations.Beta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 12:44 PM
 */
@Beta
public final class CellReflector {

    private CellReflector(){}

    public static String getLabelAsString(Object obj) {
        Field field = getLabelField(obj);
        return asString(field, obj);
    }

    public static String getValueAsString(Object obj) {
        Field field  = getValueField(obj);
        return asString(field, obj);
    }

    public static Double getValueAsDouble(Object obj) {
        Field field = getValueField(obj);
        return asDouble(field, obj);
    }

    public static int getValueAsInt(Object obj) {
        Field field = getValueField(obj);
        return asInt(field, obj);
    }

    public static byte[] getValueAsBytes(Object obj) {
        Field field = getValueField(obj);
        try {
            field.setAccessible(true);
            Object res = field.get(obj);
            return res == null ? null : (byte[])res;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw new RuntimeException("Unable to cast field value as instance of " + byte[].class.getName() +
                    ". Found field class " + field.getType().getName(), e);
        }
    }

    public static long getValueAsLong(Object obj){
        Field field = getValueField(obj);
        return asLong(field, obj);
    }

    public static <T> T getValueAsInstance(Class<T> clazz, Object obj) {
        Field field = getValueField(obj);
        return asInstance(clazz, field, obj);
    }

    public static <T> T getAuxiliaryValue(Class<T> clazz, Object obj, String name)
            throws ClassCastException{
        Field field = getNamedAuxiliaryField(obj, name);
        return asInstance(clazz, field, obj);
    }

    public static boolean hasNamedAuxiliaryField(Object obj, String name){
        return (getNamedAuxiliaryField(obj, name) != null);
    }

    public static String getColFam(Object cell)
            throws NoSuchFieldException{
        checkCellPresent(cell);
        if(hasColFamField(cell)) {
            Field field = getSingleMatchingField(cell, ColumnFamily.class);
            field.setAccessible(true);
            try {
                Object res = field.get(cell);
                if(res == null){
                    throw new NoSuchFieldException("column family for cell is null");
                }
                return String.class.cast(res);
            } catch (IllegalAccessException e){
                throw new RuntimeException(e);
            }
        } else {
            throw new NoSuchFieldException("no column family annotated for given cell type: " + cell.getClass().getName());
        }
    }

    public static boolean hasColFamField(Object cell) {
        Field[] fields = cell.getClass().getDeclaredFields();
        for(Field field: fields){
            if(field.isAnnotationPresent(ColumnFamily.class)){
                return true;
            }
        }
        return false;
    }

    public static Class<?> getValueType(Object obj)
            throws IllegalArgumentException {
        checkCellPresent(obj);
        Field field = getSingleMatchingField(obj, Value.class);
        return field.getType();
    }

    private static Field getNamedAuxiliaryField(Object obj,
                                                String name) {

        checkCellPresent(obj);
        Field[] fields = obj.getClass().getDeclaredFields();
        Field found = null;
        int foundCount = 0;
        for(Field field : fields) {
            Annotation[] anns = field.getDeclaredAnnotations();
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
                    "No field matching cell auxiliary fields with given name: " + name + ". ", CellAuxilaryField.class);
        if(foundCount > 1)
            sendFieldError("Too many auxiliary fields with matching name: " + name + ". ", CellAuxilaryField.class);
        return found;
    }

    private static <T> T asInstance(Class<T> type, Field field, Object obj) {
        try {
            field.setAccessible(true);
            return type.cast(field.get(obj));
        }  catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }  catch(ClassCastException e){
            throw new RuntimeException("Unable to cast field value as instance of " + type.getName() +
                    ". Found field class " + field.getType().getName(), e);
        }
    }

    private static String asString(Field field, Object obj) {
        return (String)getAs(String.class, field, obj);
    }

    private static int asInt(Field field, Object obj) {
        return (Integer)getAs(Integer.class, field, obj);
    }

    private static long asLong(Field field, Object obj) {
        return (Long)getAs(Long.class, field, obj);
    }

    private static double asDouble(Field field, Object obj){
        return (Double)getAs(Double.class, field, obj);
    }

    private static Object getAs(Class type, Field field, Object obj) {
        try{
            field.setAccessible(true);
            Object res = field.get(obj);
            return res == null ? null : type.cast(res);
        } catch (ClassCastException e) {
            throw new RuntimeException("Unable to cast field value as instance of " + type.getName() +
                    ". Found field class of " + field.getType().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getLabelField(Object obj) {
        Class<?> clz = obj.getClass();
        return getSingleMatchingField(obj, Label.class);
    }

    public static Field getValueField(Object obj) {
        return getSingleMatchingField(obj, Value.class);
    }

    private static Field getSingleMatchingField(Object obj,
                                                Class<? extends Annotation> clazz)
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
            sendFieldError("No field found in cell class with annotation: ", clazz);
        }
        if(foundCount > 1) {
            sendFieldError("More than one field found with annotation: ", clazz);
        }
        return found;
    }

    private static void checkCellPresent(Object obj) {
        if(!obj.getClass().isAnnotationPresent(Cell.class))
            throw new RuntimeException("Class is not annotated as a cell");
    }

    private static void sendFieldError(String msg, Class<? extends Annotation> clazz)
            throws RuntimeException{
        throw new RuntimeException(msg + clazz.getName());
    }
}
