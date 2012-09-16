package cellmate.accumulo.parameters;

import cellmate.cell.parameters.Parameters;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.security.Authorizations;

import java.util.NoSuchElementException;

/**
 * Static helper methods to generate Accumulo components from user-provided parameters
 */
public class AccumuloParameterOps {


    /**
     *  Given an instance and a set of parameters, return a Connector for
     *  the given instance. The parameters are required to have user/pass.
     *
     * @param instance Accumulo instance reference.
     * @param parameters query options containing user/pass.
     * @return Connector for the given user.
     * @throws IllegalArgumentException if missing user/pass
     * @throws RuntimeException if AccumuloSecurityException or general AccumuloException occurs.
     */
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

    /**
     * Given a connector, lookup and return the user Authorizations.
     *
     * @param connector
     * @return Authorizations for the given user.
     * @throws RuntimeException if AccumuloSecurityException or general AccumuloException occurs.
     */
    public static Authorizations getAuthsFromConnector(Connector connector) {
        try {
            return connector.securityOperations().getUserAuthorizations(connector.whoami());
        } catch (AccumuloException e) {
            throw new RuntimeException("General Accumulo error getting auths for current user: " + connector.whoami(),e);
        } catch (AccumuloSecurityException e) {
            throw new RuntimeException("Security error getting auths for current user: " + connector.whoami(),e);
        }
    }

    /**
     * Verify the supplied Parameters instance is of type {@link cellmate.accumulo.parameters.AccumuloParameters}
     *
     * @param params query parameters
     * @return AccumuloParameters
     * @throws IllegalArgumentException if parameter type is not  {@link cellmate.accumulo.parameters.AccumuloParameters}
     */
    public static AccumuloParameters checkParamType(Parameters params){
        if(!(params instanceof AccumuloParameters)){
            throw new IllegalArgumentException("ReadParameter implementation must be " +
                    AccumuloParameters.class.getName() + " to use this reader class " +
                    " instead found " + params.getClass().getName());
        }
        return (AccumuloParameters)params;
    }



}
