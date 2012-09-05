package cellmate.accumulo.reader;

import cellmate.reader.ReadParameters;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 11:09 PM
 */
public class AccumuloReadParameters implements ReadParameters {

    public static final String MAX_RESULTS = "cellmate.read.max.results.param";
    public static final String START_KEY = "cellmate.read.row.start.key";
    public static final String END_KEY = "cellmate.read.row.end.key";
    public static final String COLUMN_FAMILES_AND_QUALS = "cellmate.read.scan.col.fams.and.quals";
    public static final String ZOOKEEPERS = "cellmate.read.accumulo.zookeepers.list";
    public static final String INSTANCE_NAME = "cellmate.read.accumulo.instance.name";
    public static final String USER = "cellmate.read.accumulo.user";
    public static final String PASSWORD = "cellmate.read.accumulo.password";
    public static final String TABLE_NAME = "cellmate.read.scan.table.name";
    public static final String BATCH_SIZE = "cellmate.read.batch.scan.size";

    Map<String, Object> propertyMap;

    private AccumuloReadParameters(Builder builder){
        propertyMap = builder.propertyMap;
    }


    public int getMaxResults() {
        try {
            return getInt(MAX_RESULTS);
        } catch (NoSuchElementException e){
            return Integer.MAX_VALUE;
        }
    }

    public String getStartKey() {
        return getString(START_KEY);
    }

    public String getEndKey() {
        return getString(END_KEY);
    }

    public int getBatchSize(){
        return getInt(BATCH_SIZE);
    }

    public String getZookeepers()
        throws NoSuchElementException{
        return getString(ZOOKEEPERS);
    }

    public String getInstanceName()
        throws NoSuchElementException{
        return getString(INSTANCE_NAME);
    }

    public String getTableName()
        throws NoSuchElementException{
        return getString(TABLE_NAME);
    }

    public String getUser()
        throws NoSuchElementException{
        return getString(USER);
    }

    public String getPassword()
        throws NoSuchElementException{
        return getString(PASSWORD);
    }

    public String[] getColFamsAndQuals()
            throws NoSuchElementException{
        return getStringArray(COLUMN_FAMILES_AND_QUALS);
    }

    private String[] getStringArray(String paramName)
        throws NoSuchElementException {
        try {
            Object obj = checkAndGet(paramName);
            return String[].class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as String[]",e);
        }
    }


    public int getInt(String paramName)
            throws NoSuchElementException {
        try {
            Object obj = checkAndGet(paramName);
            return Integer.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as int", e);
        }
    }

    public long getLong(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return Long.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as long", e);
        }
    }

    public String getString(String paramName) {
        try {
            Object obj = checkAndGet(paramName);
            return String.class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as String", e);
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

        public AccumuloReadParameters build() {
            return new AccumuloReadParameters(this);
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

        public Builder setColFams(String[] colFams){
            propertyMap.put(COLUMN_FAMILES_AND_QUALS, colFams);
            return this;
        }

        public Builder setZookeepers(String zookeepers){
            propertyMap.put(ZOOKEEPERS, zookeepers);
            return this;
        }

        public Builder setInstanceName(String instanceName) {
            propertyMap.put(INSTANCE_NAME, instanceName);
            return this;
        }

        public Builder setUser(String user){
            propertyMap.put(USER, user);
            return this;
        }

        public Builder setPassword(String password){
            propertyMap.put(PASSWORD, password);
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
