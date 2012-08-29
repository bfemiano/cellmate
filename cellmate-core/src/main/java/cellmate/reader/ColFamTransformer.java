package cellmate.reader;

/**
 * User: bfemiano
 * Date: 8/27/12
 * Time: 6:50 PM
 */
public interface ColFamTransformer<I> {

    public boolean isMatch(I record);
    public String getLabelForColFam(I record);
}
