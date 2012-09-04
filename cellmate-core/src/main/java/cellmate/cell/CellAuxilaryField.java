package cellmate.cell;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:49 PM
 */
@Beta
@Target(value={ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CellAuxilaryField {
    String name();
}
