package cellmate.exception;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 4:53 PM
 */
public class NullDataForLabelValueException extends Exception{

    public NullDataForLabelValueException(String msg, Throwable e) {
        super(msg, e);
    }

    public NullDataForLabelValueException(String s) {
        super(s);
    }
}
