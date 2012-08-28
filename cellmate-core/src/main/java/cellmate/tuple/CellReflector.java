package cellmate.tuple;

import cellmate.exception.NullDataForLabelValueException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 12:44 PM
 */
public class CellReflector {

    public static String getLabelAsString(Object obj)
        throws NullDataForLabelValueException {
        Field field = getLabelField(obj);
        return asString(field, obj);
    }

    public static String getValueAsString(Object obj)
        throws NullDataForLabelValueException {
        Field field  = getValueField(obj);
        return asString(field, obj);
    }

    public static int getValueAsInt(Object obj)
        throws NullDataForLabelValueException {
        Field field = getValueField(obj);
        return asInt(field, obj);
    }

    public static byte[] getValueAsBytes(Object obj)
        throws NullDataForLabelValueException {
        Field field = getValueField(obj);
        try {
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res == null)
                throw new NullDataForLabelValueException("found null for data/value");
            return (byte[])res;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getValueAsLong(Object obj)
        throws NullDataForLabelValueException {
        Field field = getValueField(obj);
        return asLong(field, obj);
    }

    public static <T> T getValueAsInstance(Class<T> clazz, Object obj) {
        Field field = getValueField(obj);
        return asInstance(clazz, field, obj);
    }

    @SuppressWarnings(value="unchecked")
    public static <T> T getAuxiliaryValue(Object obj, String name)
        throws ClassCastException{
        Field field = getNamedAuxiliaryField(obj, name);
        try {
            field.setAccessible(true);
            T result = (T)field.get(obj);
            if(result == null){
                throw new RuntimeException("Found auxiliary field (" + name + ") for instance, but value was null");
            }
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
            sendFieldError("Did you forget to name the auxilary field?. No field matching given annotation name " + name + " ", CellAuxilaryField.class);
        if(foundCount > 1)
            sendFieldError("Too many auxiliary fields with matching name: " + name, CellAuxilaryField.class);
        return found;
    }

    private static <T> T asInstance(Class<T> clazz, Field field, Object obj) {
        try {
            field.setAccessible(true);
            return clazz.cast(field.get(obj));
        }  catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }  catch(ClassCastException e){
            throw new RuntimeException("Unable to cast to parameterized type for asInstance ",e);
        }
    }

    private static String asString(Field field, Object obj)
        throws NullDataForLabelValueException {
        return (String)getAs(String.class, field, obj);
    }

    private static int asInt(Field field, Object obj)
        throws NullDataForLabelValueException {
        return (Integer)getAs(Integer.class, field, obj);
    }

    private static long asLong(Field field, Object obj)
        throws NullDataForLabelValueException {
        return (Long)getAs(Long.class, field, obj);
    }

    private static Object getAs(Class type, Field field, Object obj)
        throws NullDataForLabelValueException {
        try{
            field.setAccessible(true);
            Object res = field.get(obj);
            if(res == null)
                throw new NullDataForLabelValueException("found null for data/value");
            return type.cast(res);
        } catch (ClassCastException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getLabelField(Object obj) {
        Class<?> clz = obj.getClass();
        return getSingleMatchingField(obj, Label.class);
    }

    private static Field getValueField(Object obj) {
        Class<?> clz = obj.getClass();
        return getSingleMatchingField(obj, Value.class);
    }

    private static Field getSingleMatchingField(Object obj,
                                                Class<? extends Annotation> clazz)
            throws RuntimeException {

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
