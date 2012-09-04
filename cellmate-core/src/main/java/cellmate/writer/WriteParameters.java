package cellmate.writer;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 6:40 PM
 */
public interface WriteParameters {

    public int getInt(String paramName);
    public long getLong(String paramName);
    public String getString(String paramName);
}
