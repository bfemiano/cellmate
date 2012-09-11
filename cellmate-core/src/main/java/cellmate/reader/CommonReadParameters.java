package cellmate.reader;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 3:08 PM
 */
public class CommonReadParameters implements ReadParameters {

    public static final String MAX_RESULTS = "cellmate.max.results.param";
    public static final String TABLE_NAME = "cellmate.populate.populate.table.name";
    public static final String BATCH_SIZE = "cellmate.populate.batch.populate.size";
    public static final String CF_AND_QUAL_LIST = "cellmate.populate.populate.col.fams.and.quals";
    public static final String START_KEY = "cellmate.populate.row.start.key";
    public static final String END_KEY = "cellmate.populate.row.end.key";

    Map<String, Object> propertyMap;

    private CommonReadParameters(Builder builder){
        propertyMap = builder.propertyMap;
    }


    public int getMaxResults() {
        try {
            return getInt(MAX_RESULTS);
        } catch (NoSuchElementException e){
            return Integer.MAX_VALUE;
        }
    }

    public int getBatchSize(){
        return getInt(BATCH_SIZE);
    }

    public String getTableName()
        throws NoSuchElementException{
        return getString(TABLE_NAME);
    }

    public String getStartKey()
        throws NoSuchElementException{
        return getString(START_KEY);
    }

    public String getEndKey()
        throws NoSuchElementException{
        return getString(END_KEY);
    }

    public String[] getColumns()
            throws NoSuchElementException{
        return getStrings(CF_AND_QUAL_LIST);
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

    public boolean getBoolean(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return Boolean.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as boolean", e);
        }
    }

    public byte[] getBytes(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return byte[].class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as byte array", e);
        }
    }

    public <T> T getObjectAs(Class<T> cls, String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return cls.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as byte array", e);
        }
    }



    public String[] getStrings(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return String[].class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as String array", e);
        }
    }

    public Object checkAndGet(String paramName) {
        if(!propertyMap.containsKey(paramName))
            throw new NoSuchElementException(" no key found for property name: " + paramName);
        Object obj = propertyMap.get(paramName);
        if(obj == null)
            throw new NoSuchElementException(" null value for property: " +  paramName);
        return obj;
    }

    public static class Builder{

        Map<String, Object> propertyMap = new HashMap<String, Object>();

        public CommonReadParameters build() {
            return new CommonReadParameters(this);
        }

        public Builder setEndKey(String endKey) {
            propertyMap.put(END_KEY, endKey);
            return this;
        }

        public Builder setMaxResults(int maxResults) {
            propertyMap.put(MAX_RESULTS, maxResults);
            return this;
        }

        public Builder setBatchSize(int batchSize) {
            propertyMap.put(BATCH_SIZE, batchSize);
            return this;
        }

        public Builder setStartKey(String startKey) {
            propertyMap.put(START_KEY, startKey);
            return this;
        }

        public Builder setColumns(String[] colFams){
            propertyMap.put(CF_AND_QUAL_LIST, colFams);
            return this;
        }

        public Builder setTableName(String tableName) {
            propertyMap.put(TABLE_NAME, tableName);
            return this;
        }


        public Builder addNamedProperty(String name, Object value){
            propertyMap.put(name,value);
            return this;
        }
    }

}
