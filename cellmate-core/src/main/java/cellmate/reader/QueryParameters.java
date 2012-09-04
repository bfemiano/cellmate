package cellmate.reader;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 3:06 PM
 */
public interface QueryParameters {

    public int getInt(String paramName);
    public long getLong(String paramName);
    public String getString(String paramName);
}
