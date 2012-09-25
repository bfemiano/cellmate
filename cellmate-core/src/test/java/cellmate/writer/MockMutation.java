package cellmate.writer;

import org.testng.collections.Lists;

import java.util.List;

/**
 * User: bfemiano
 * Date: 9/4/12
 * Time: 6:27 PM
 */
public class MockMutation {

    private String rowId;

    private List<MockColQualVal> items = Lists.newArrayList();

    public MockMutation(String rowId){
        this.rowId = rowId;
    }

    public void addItem(MockColQualVal item){
        items.add(item);
    }

    public List<MockColQualVal> getItems(){
        return items;
    }

    public String getRowId() {
        return rowId;
    }

    public static class MockColQualVal {
        private String colFam;
        private String qual;
        private byte[] value;

        public MockColQualVal(String colFam, String qual, byte[] value) {
            this.colFam = colFam;
            this.qual = qual;
            this.value = value.clone();
        }

        public String getColFam() {
            return colFam;
        }

        public String getQual() {
            return qual;
        }

        public byte[] getValue() {
            return value.clone();
        }
    }
}
