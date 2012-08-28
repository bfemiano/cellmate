package cellmate.tuple;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:49 PM
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CellAuxilaryField {

    String name();
}
