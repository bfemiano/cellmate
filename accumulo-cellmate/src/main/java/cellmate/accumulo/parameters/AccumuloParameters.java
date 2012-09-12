package cellmate.accumulo.parameters;

import cellmate.cell.parameters.CommonParameters;
import cellmate.cell.parameters.Parameters;
import com.google.common.collect.Lists;
import org.apache.accumulo.core.client.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 11:09 PM
 */
public class AccumuloParameters implements Parameters {

    public static final String ZOOKEEPERS = "cellmate.read.accumulo.zookeepers.list";
    public static final String INSTANCE_NAME = "cellmate.read.accumulo.instance.name";
    public static final String USER = "cellmate.read.accumulo.user";
    public static final String PASSWORD = "cellmate.read.accumulo.password";
    public static final String ITERATOR_PREFIX = "cellmate.read.accumulo.iterator.prefix";
    public static final String MAX_MEMORY_FOR_WRITE = "cellmate.write.max.memory";
    public static final String MAX_LATENCY_FOR_WRITE = "cellmate.write.max.latency";
    public static final String MAX_WRITE_THREADS = "cellmate.write.max.threads";

    private static final long DEFAULT_MAX_MEMORY= 10000L;
    private static final long DEFAULT_MAX_LATENCY=1000L;
    private static final int DEFAULT_MAX_WRITE_THREADS = 4;

    private Map<String, Object> propertyMap;
    private CommonParameters commonParameters;

    private AccumuloParameters(Builder builder){
        propertyMap = builder.propertyMap;
        commonParameters = builder.commonParamBuilder.build();
    }

    public int getMaxResults() {
        return commonParameters.getMaxResults();
    }

    public String getStartKey() {
        return commonParameters.getStartKey();
    }

    public String getEndKey() {
        return commonParameters.getEndKey();
    }

    public int getBatchSize(){
        return commonParameters.getBatchSize();
    }

    public String[] getColumns(){
        return commonParameters.getColumns();
    }

    public String getZookeepers()
            throws NoSuchElementException{
        return getString(ZOOKEEPERS);
    }

    public String getInstanceName()
            throws NoSuchElementException{
        return getString(INSTANCE_NAME);
    }

    public long getMaxWriteMemory() {
        try {
           return getLong(MAX_MEMORY_FOR_WRITE);
        } catch (NoSuchElementException e){
            return DEFAULT_MAX_MEMORY;
        }
    }

    public long getMaxWriteLatency() {
        try {
           return getLong(MAX_LATENCY_FOR_WRITE);
        } catch (NoSuchElementException e){
            return DEFAULT_MAX_LATENCY;
        }
    }

    public int getMaxWriteThreads() {
        try {
           return getInt(MAX_WRITE_THREADS);
        } catch (NoSuchElementException e){
            return DEFAULT_MAX_WRITE_THREADS;
        }
    }

    public List<IteratorSetting> getIterators() {
        List<IteratorSetting> iterators = Lists.newArrayList();
        for(String key : propertyMap.keySet()) {
             if(key.contains(ITERATOR_PREFIX)) {
                 iterators.add(getObjectAs(IteratorSetting.class, key));
             }
        }
        return iterators;
    }

    public String getTableName()
            throws NoSuchElementException{
        return commonParameters.getTableName();
    }

    public String getUser()
            throws NoSuchElementException{
        return getString(USER);
    }

    public String getPassword()
            throws NoSuchElementException{
        return getString(PASSWORD);
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
            throw new RuntimeException("could not cast value as " + cls.getName(), e);
        }
    }

    public String[] getStrings(String paramName)
            throws NoSuchElementException {
        try {
            Object obj = checkAndGet(paramName);
            return String[].class.cast(obj);
        } catch (ClassCastException e){
            throw new RuntimeException("could not cast value as String[]",e);
        }
    }


    public Object checkAndGet(String paramName) {
        Object obj;

        if(propertyMap.containsKey(paramName)) {
            obj = propertyMap.get(paramName);
            if(obj == null)
                throw new NoSuchElementException(" null value for property: " +  paramName);
        } else {
            return commonParameters.checkAndGet(paramName);
        }
        return obj;
    }

    public static class Builder{

        Map<String, Object> propertyMap = new HashMap<String, Object>();
        CommonParameters.Builder commonParamBuilder = new CommonParameters.Builder();
        private int iteratorPrefix;

        public AccumuloParameters build() {
            return new AccumuloParameters(this);
        }

        public Builder setEndKey(String endKey) {
            commonParamBuilder.setEndKey(endKey);
            return this;
        }

        public Builder setColumns(String[] columns) {
            commonParamBuilder.setColumns(columns);
            return this;
        }

        public Builder setMaxResults(int maxResults) {
            commonParamBuilder.setMaxResults(maxResults);
            return this;
        }

        public Builder setStartKey(String startKey) {
            commonParamBuilder.setStartKey(startKey);
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

        public Builder setBatchSize(int batchSize) {
            commonParamBuilder.setBatchSize(batchSize);
            return this;
        }

        public Builder setPassword(String password){
            propertyMap.put(PASSWORD, password);
            return this;
        }

        public Builder setMaxWriteMemory(String password){
            propertyMap.put(MAX_MEMORY_FOR_WRITE, password);
            return this;
        }

        public Builder setMaxWriteLatency(String password){
            propertyMap.put(MAX_LATENCY_FOR_WRITE, password);
            return this;
        }

        public Builder setMaxWriteThreads(String password){
            propertyMap.put(MAX_WRITE_THREADS, password);
            return this;
        }

        public Builder setTable(String tableName){
            commonParamBuilder.setTableName(tableName);
            return this;
        }

        public Builder addIteratorSetting(IteratorSetting iterator){
            propertyMap.put(ITERATOR_PREFIX + (iteratorPrefix++), iterator);
            return this;
        }

        public Builder addNamedProperty(String name, Object value){
            propertyMap.put(name,value);
            return this;
        }
    }
}
