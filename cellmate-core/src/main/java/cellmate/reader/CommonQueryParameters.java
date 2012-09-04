package cellmate.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 3:08 PM
 */
public class CommonQueryParameters implements QueryParameters{

    public static final String MAX_RESULTS = "cellmate.max.results.param";
    public static final String START_KEY = "cellmate.row.start.key";
    public static final String END_KEY = "cellmate.row.end.key";

    Map<String, Object> propertyMap;

    private CommonQueryParameters(Builder builder){
        propertyMap = builder.propertyMap;
    }


    public int getMaxResults() {
       try {
           return getInt(MAX_RESULTS);
       } catch (NoSuchElementException e){
           return Integer.MAX_VALUE;
       }
    }


    public int getInt(String paramName)
            throws NoSuchElementException {
        try {
            Object obj = checkAndGet(paramName);
            return Integer.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast as value as int", e);
        }
    }

    public long getLong(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return Long.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast as value as long", e);
        }
    }

    public String getString(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return String.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast as value as String", e);
        }
    }

    private Object checkAndGet(String paramName) {
        if(!propertyMap.containsKey(paramName))
            throw new NoSuchElementException(" no key found for property name: " + paramName);
        Object obj = propertyMap.get(paramName);
        if(obj == null)
            throw new NoSuchElementException(" null value for property: " +  paramName);
        return obj;
    }

    public static class Builder{

        Map<String, Object> propertyMap = new HashMap<String, Object>();

        public CommonQueryParameters build() {
            return new CommonQueryParameters(this);
        }

        public Builder setEndKey(String endKey) {
            propertyMap.put(END_KEY, endKey);
            return this;
        }

        public Builder setMaxResults(int maxResults) {
            propertyMap.put(MAX_RESULTS, maxResults);
            return this;
        }

        public Builder setStartKey(String startKey) {
            propertyMap.put(START_KEY, startKey);
            return this;
        }

        public Builder addNamedProperty(String name, Object value){
            propertyMap.put(name,value);
            return this;
        }
    }

}
