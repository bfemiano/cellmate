package cellmate.reader;

import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 3:06 PM
 */
public interface ReadParameters {

    public int getInt(String paramName);
    public long getLong(String paramName);
    public String getString(String paramName);
    public boolean getBoolean(String paramName);
    public byte[] getBytes(String paramName);
    public <T> T getObjectAs(Class<T> cls, String paramName);
    public String[] getStrings(String paramName);
}
