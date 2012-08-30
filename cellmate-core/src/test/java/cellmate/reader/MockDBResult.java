package cellmate.reader;

/**
 * User: bfemiano
 * Date: 8/28/12
 * Time: 10:52 PM
 */
public final class MockDBResult {

    private String id;
    private String colfam;
    private String qual;
    private String val;
    private long timestamp;

    public MockDBResult(String id, String colfam, String qual, String val) {
        this.id = id;
        this.colfam = colfam;
        this.qual = qual;
        this.val = val;
    }

    public MockDBResult(String id, String colfam, String qual, String val, long timestamp) {
        this.id = id;
        this.colfam = colfam;
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

    public String getColfam() {
        return colfam;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
