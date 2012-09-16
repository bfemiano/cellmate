package cellmate.cell;

import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Required for Cells to be used on write.
 */
@Beta
@Target(value={ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnFamily {

}
