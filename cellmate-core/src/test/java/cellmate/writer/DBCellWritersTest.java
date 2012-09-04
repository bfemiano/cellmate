package cellmate.writer;

import cellmate.cell.IntValueCell;
import cellmate.cell.StringValueCell;
import cellmate.cell.Tuple;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

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
    }

    //more tests for robustness. figure some out.

}
