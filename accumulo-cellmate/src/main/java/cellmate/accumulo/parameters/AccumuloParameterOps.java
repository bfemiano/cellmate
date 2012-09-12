package cellmate.accumulo.parameters;

import cellmate.cell.parameters.Parameters;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.security.Authorizations;

import java.util.NoSuchElementException;

/**
 * User: bfemiano
 * Date: 9/12/12
 * Time: 2:09 PM
 */
public class AccumuloParameterOps {



    public static Connector getConnectorFromParameters(Instance instance, AccumuloParameters parameters) {
        Connector connector;
        try {
            String user = parameters.getUser();
            String pass = parameters.getPassword();
            connector = instance.getConnector(user, pass);
        } catch (NoSuchElementException e){
            throw new IllegalArgumentException("missing user/pass");
        } catch (AccumuloSecurityException e) {
            throw new RuntimeException("Security error trying to establish populate connector",e);
        } catch (AccumuloException e) {
            throw new RuntimeException("General Accumulo error while setting up populate connector",e);
        }
        return connector;
    }

    public static Authorizations getAuthsFromConnector(Connector connector) {
        try {
            return connector.securityOperations().getUserAuthorizations(connector.whoami());
        } catch (AccumuloException e) {
            throw new RuntimeException("General Accumulo error getting auths for current user: " + connector.whoami(),e);
        } catch (AccumuloSecurityException e) {
            throw new RuntimeException("Security error getting auths for current user: " + connector.whoami(),e);
        }
    }

    public static AccumuloParameters checkParamType(Parameters params){
        if(!(params instanceof AccumuloParameters)){
            throw new IllegalArgumentException("ReadParameter implementation must be " +
                    AccumuloParameters.class.getName() + " to use this reader class " +
                    " instead found " + params.getClass().getName());
        }
        return (AccumuloParameters)params;
    }



}
