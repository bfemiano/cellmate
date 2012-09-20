package cellmate.cell;


import com.google.common.annotations.Beta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Required for use with the extractor and writer APIs.
 * See {@link cellmate.extractor.SingleMultiValueCellExtractor} and
 * {@link cellmate.writer.DBRecordWriter}
 *
 */
@Beta
@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cell {

}
