package cellmate.reader;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:52 PM
 */
public final class MockDBResult {

    private String id;
    private String colFam;
    private String qual;
    private String val;
    private long timestamp;

    public MockDBResult(String id, String colFam, String qual, String val) {
        this.id = id;
        this.colFam = colFam;
        this.qual = qual;
        this.val = val;
    }

    public MockDBResult(String id, String colFam, String qual, String val, long timestamp) {
        this.id = id;
        this.colFam = colFam;
        this.qual = qual;
        this.val = val;
        this.timestamp = timestamp;
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

    public String getColFam() {
        return colFam;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
