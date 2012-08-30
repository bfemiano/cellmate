package cellmate.cell;

import com.google.common.annotations.Beta;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:49 PM
 */
@Beta
@Retention(RetentionPolicy.RUNTIME)
public @interface CellAuxilaryField {

    String name();
}
