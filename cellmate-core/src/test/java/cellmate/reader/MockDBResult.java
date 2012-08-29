package cellmate.reader;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:52 PM
 */
public final class MockDBResult {

    private String id;
    private String qual;
    private String val;

    public MockDBResult(String id, String qual, String val) {
        this.id = id;
        this.qual = qual;
        this.val = val;
    }

    public String getId() {
        return id;
    }

    public String getQual() {
        return qual;
    }

    public String getVal() {
        return val;
    }
}
