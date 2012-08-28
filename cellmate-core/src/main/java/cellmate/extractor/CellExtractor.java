package cellmate.extractor;

import cellmate.tuple.Cell;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 7:03 PM
 */
public interface CellExtractor {

    public <T> List<T> matchLabel(List<T> tuples, String label);
}
