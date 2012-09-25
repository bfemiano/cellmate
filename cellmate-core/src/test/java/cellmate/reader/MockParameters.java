package cellmate.reader;

import cellmate.cell.parameters.CommonParameters;
import cellmate.cell.parameters.Parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 3:08 PM
 */
public class MockParameters implements Parameters {

    public static final String MAX_RESULTS = "cellmate.max.results.param";

    private Map<String, Object> propertyMap;
    private CommonParameters commonParameters;

    private MockParameters(Builder builder){
        propertyMap = builder.propertyMap;
        commonParameters = builder.commonParamBuilder.build();
    }


    public int getMaxResults() {
       return commonParameters.getMaxResults();
    }

    public int getInt(String paramName)
            throws NoSuchElementException {
        return commonParameters.getInt(paramName);
    }

    public long getLong(String paramName) {
        return commonParameters.getLong(paramName);
    }

    public String getString(String paramName) {
        return commonParameters.getString(paramName);
    }

    public boolean getBoolean(String paramName) {
        return commonParameters.getBoolean(paramName);
    }

    public byte[] getBytes(String paramName) {
        return commonParameters.getBytes(paramName);
    }

    public <T> T getObjectAs(Class<T> cls, String paramName) {
        return commonParameters.getObjectAs(cls, paramName);
    }

    public String[] getStrings(String paramName) throws NoSuchElementException {
        return commonParameters.getStrings(paramName);
    }

    public boolean hasKey(String paramName) {
        return propertyMap.containsKey(paramName);
    }

    @SuppressWarnings("unused")
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
        CommonParameters.Builder commonParamBuilder = new CommonParameters.Builder();

        public MockParameters build() {
            return new MockParameters(this);
        }

        public Builder setMaxResults(int maxResults) {
            commonParamBuilder.setMaxResults(maxResults);
            return this;
        }


        public Builder addNamedProperty(String name, Object value){
            commonParamBuilder.addNamedProperty(name, value);
            return this;
        }
    }

}
