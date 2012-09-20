package cellmate.accumulo.reader.scan;

import cellmate.accumulo.parameters.AccumuloParameters;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.core.security.Authorizations;

import java.util.Map;

/**
 * Returns the internal scan
 */
public interface Scan {

    Iterable<Map.Entry<Key,Value>> get();
}
