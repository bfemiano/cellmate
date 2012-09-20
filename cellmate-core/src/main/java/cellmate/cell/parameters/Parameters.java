package cellmate.cell.parameters;

import java.util.NoSuchElementException;

/**
 * getters for common types of primative and instance parameter arguments.
 */
public interface Parameters {

    public int getInt(String paramName);
    public long getLong(String paramName);
    public String getString(String paramName);
    public boolean getBoolean(String paramName);
    public byte[] getBytes(String paramName);
    public <T> T getObjectAs(Class<T> cls, String paramName);
    public String[] getStrings(String paramName);
    public boolean hasKey(String paramName);
}
