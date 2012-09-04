package cellmate.extractor;

/**
 * User: bfemiano
 * Date: 9/3/12
 * Time: 11:17 PM
 */
public class CellExtractorException extends Exception {

    public CellExtractorException(String msg){
        super(msg);
    }

    public CellExtractorException(String msg, Throwable e){
        super(msg, e);
    }
}
