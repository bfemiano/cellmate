package cellmate.writer;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 1:59 AM
 */

import cellmate.cell.Tuple;
import com.google.common.annotations.Beta;

@Beta
public interface DBRecordWriter<D,C>  {

    public D getFromTuple(D dbItem, Tuple<C> tuple);

    public void write(D dbItem);
}
