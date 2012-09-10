package cellmate.extractor;

/**
 * User: bfemiano
 * Date: 9/3/12
 * Time: 11:17 PM
 */
public class CellExtractorException extends Exception {

    private ErrorType type;

    public CellExtractorException(String msg, ErrorType type){
        super(msg);
        this.type = type;
    }

    public CellExtractorException(Throwable e, ErrorType type){
        super(e);
        this.type = type;
    }

    public CellExtractorException(String msg, Throwable e, ErrorType type){
        super(msg, e);
        this.type = type;
    }

    public ErrorType getType() {
        return this.type;
    }
}
