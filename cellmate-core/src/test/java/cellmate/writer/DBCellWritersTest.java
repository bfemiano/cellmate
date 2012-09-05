package cellmate.writer;

import cellmate.cell.CellReflector;
import cellmate.cell.IntValueCell;
import cellmate.cell.StringValueCell;
import cellmate.cell.Tuple;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.w3c.dom.events.MutationEvent;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 2:02 AM
 */
public class DBCellWritersTest {

    private DBRecordWriter<MockMutation, StringValueCell> writer;
    private Tuple<StringValueCell> row1;
    private Tuple<StringValueCell> row2;
    private Tuple<IntValueCell> row3;
    private Tuple<IntValueCell> row4;

    ImmutableList<Tuple<StringValueCell>> tuplesWithStrings;
    ImmutableList<Tuple<IntValueCell>> tupleWithInts;

    @BeforeClass
    public void setup() {
        writer = new MockDBCellValueWriter<StringValueCell>();

        StringValueCell cell1 = new StringValueCell("name", "brian", "info");
        StringValueCell cell2 = new StringValueCell("age", "11", "info");
        StringValueCell cell3 = new StringValueCell("height", "6ft");
        row1 = new Tuple<StringValueCell>("row1");
        row1.addCell(cell1);
        row1.addCell(cell2);
        row1.addCell(cell3);

        row2 = new Tuple<StringValueCell>("row2");
        row2.addCell(cell1);
        row2.addCell(cell2);
        row2.addCell(cell3);


        IntValueCell cell4 = new IntValueCell("age", 13);
        IntValueCell cell5 = new IntValueCell("siblings", 1);
        row3 = new Tuple<IntValueCell>("row3");
        row3.addCell(cell4);
        row3.addCell(cell5);

        row4 = new Tuple<IntValueCell>("row4");
        row4.addCell(cell4);
        row4.addCell(cell5);

        ImmutableList.Builder<Tuple<StringValueCell>> builder1 = ImmutableList.builder();
        builder1.add(row1);
        builder1.add(row2);
        tuplesWithStrings = builder1.build();

        ImmutableList.Builder<Tuple<IntValueCell>> builder2 = ImmutableList.builder();
        builder2.add(row3);
        builder2.add(row4);
        tupleWithInts = builder2.build();

    }

    @Test
    public void mixedColumnFamiles() {
        CommonWriteParameters parameters = new CommonWriteParameters.Builder().build();

        ImmutableList<MockMutation> mutations = writer.write(tuplesWithStrings, parameters);
        assertNotNull(mutations);
        assertEquals(mutations.size(), 2);
        int foundName = 0;
        int foundAge = 0;
        int foundHeight = 0;
        int foundCfColFam = 0;
        int foundInfoColFam = 0;
        for(MockMutation mut : mutations){
            if(!mut.getRowId().equals("row1") & !mut.getRowId().equals("row2")){
                fail("row id other than row1 or row2 found");
            }
            for(MockMutation.MockColQualVal item : mut.getItems()){
                if(item.getColFam().equals("cf")) {
                    foundCfColFam++;
                } else if(item.getColFam().equals("info")){
                    foundInfoColFam++;
                } else {
                    fail();
                }
                if(item.getQual().equals("name")){
                    foundName++;
                }
                if(item.getQual().equals("age")){
                    foundAge++;
                }
                if(item.getQual().equals("height")){
                    foundHeight++;
                }
            }
        }
        assertEquals(foundName, 2);
        assertEquals(foundAge, 2);
        assertEquals(foundHeight, 2);
        assertEquals(foundInfoColFam, 4);
        assertEquals(foundCfColFam, 2);
    }

    @Test
    public void verifyByteContents() {
        assertEquals(tupleWithInts.size(), 2);
        assertEquals(tupleWithInts.get(0).getInternalList().size(), 2);
        assertEquals(tupleWithInts.get(1).getInternalList().size(), 2);

        DBRecordWriter<MockMutation, IntValueCell> writer = new MockDBCellValueWriter<IntValueCell>();
        CommonWriteParameters parameters = new CommonWriteParameters.Builder().build();

        ImmutableList<MockMutation> mutations = writer.write(tupleWithInts, parameters);
        assertNotNull(mutations);
        assertEquals(mutations.size(), 2);
        assertEquals(mutations.get(0).getItems().size(), 2);
        boolean verifiedIntBytes = false;
        for(MockMutation.MockColQualVal item : mutations.get(0).getItems()){
            if(item.getQual().equals("age")){
                byte[] value = item.getValue();
                byte[] testInt = new byte[4];
                ByteBuffer.wrap(testInt).putInt(13);
                assertEquals(testInt, value);
                verifiedIntBytes = true;
            }
        }
        assertTrue(verifiedIntBytes);
    }

    //few more checks.

}
