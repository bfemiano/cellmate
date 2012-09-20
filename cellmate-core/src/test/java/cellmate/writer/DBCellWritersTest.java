package cellmate.writer;

import cellmate.cell.IntValueCell;
import cellmate.cell.StringValueCell;
import cellmate.cell.CellGroup;
import cellmate.cell.parameters.CommonParameters;
import cellmate.extractor.CellExtractorException;
import cellmate.extractor.CellReflector;
import cellmate.extractor.ErrorType;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

import java.util.List;

/**
 * User: bfemiano
 * Date: 8/29/12
 * Time: 2:02 AM
 */
public class DBCellWritersTest {

    private DBRecordWriter<MockMutation> writer;

    ImmutableList<CellGroup<StringValueCell>> group1;
    ImmutableList<CellGroup<StringValueCell>> group2;

    private DBItemTransformer<MockMutation, StringValueCell> stringTransformer;

    @BeforeClass
    public void setup() {
        writer = new MockDBCellValueWriter();

        StringValueCell cell1 = new StringValueCell("name", "brian", "info");
        StringValueCell cell2 = new StringValueCell("age", "11", "info");
        StringValueCell cell3 = new StringValueCell("height", "6ft", "info");
        CellGroup<StringValueCell> row1 = new CellGroup<StringValueCell>("row1");
        row1.addCell(cell1);
        row1.addCell(cell2);
        row1.addCell(cell3);

        CellGroup<StringValueCell> row2 = new CellGroup<StringValueCell>("row2");
        row2.addCell(cell1);
        row2.addCell(cell2);
        row2.addCell(cell3);


        StringValueCell cell4 = new StringValueCell("age", "13", "cf");
        StringValueCell cell5 = new StringValueCell("siblings", "1", "cf");
        CellGroup<StringValueCell> row3 = new CellGroup<StringValueCell>("row3");
        row3.addCell(cell4);
        row3.addCell(cell5);

        CellGroup<StringValueCell> row4 = new CellGroup<StringValueCell>("row4");
        row4.addCell(cell4);
        row4.addCell(cell5);

        ImmutableList.Builder<CellGroup<StringValueCell>> builder1 = ImmutableList.builder();
        builder1.add(row1);
        builder1.add(row2);
        group1 = builder1.build();

        ImmutableList.Builder<CellGroup<StringValueCell>> builder2 = ImmutableList.builder();
        builder2.add(row3);
        builder2.add(row4);
        group2 = builder2.build();

        stringTransformer = new DBItemTransformer<MockMutation, StringValueCell>() {
            public List<MockMutation> apply(CellGroup<StringValueCell> cellGroup)
                    throws CellExtractorException {
                ImmutableList.Builder<MockMutation> list = ImmutableList.builder();
                MockMutation result = new MockMutation(cellGroup.getTag());
                for(StringValueCell cell : cellGroup.getInternalList()) {
                    String qual = null;
                    qual = CellReflector.getLabelAsString(cell);
                    byte[] valueBytes = CellReflector.getValueBytesIfPrimative(cell);
                    String colFam;
                    colFam = CellReflector.getColFam(cell);
                    result.addItem(new MockMutation.MockColQualVal(colFam, qual, valueBytes));
                }
                list.add(result);
                return list.build();
            }
        };
    }

    @Test
    public void mixedColumnFamiles() {
        CommonParameters parameters = new CommonParameters.Builder().build();

        ImmutableList<MockMutation> mutations = null;
        try {
            mutations = writer.write(group1, parameters, stringTransformer);
        } catch (CellExtractorException e) {
            fail("cell extraction error",e);
        }
        assertNotNull(mutations);
        assertEquals(mutations.size(), 2);
        int foundName = 0;
        int foundAge = 0;
        int foundHeight = 0;
        int foundInfoColFam = 0;
        for(MockMutation mut : mutations){
            if(!mut.getRowId().equals("row1") & !mut.getRowId().equals("row2")){
                fail("row id other than row1 or row2 found");
            }
            for(MockMutation.MockColQualVal item : mut.getItems()){
                if(item.getColFam().equals("info")){
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
        assertEquals(foundInfoColFam, 6);
    }

    @Test
    public void verifyByteContents() {
        assertEquals(group2.size(), 2);
        assertEquals(group2.get(0).getInternalList().size(), 2);
        assertEquals(group2.get(1).getInternalList().size(), 2);

        DBRecordWriter<MockMutation> writer = new MockDBCellValueWriter();
        CommonParameters parameters = new CommonParameters.Builder().build();

        ImmutableList<MockMutation> mutations = null;
        try {
            mutations = writer.write(group2, parameters, stringTransformer);
        } catch (CellExtractorException e) {
            fail("cell extraction exception");
        }
        assertNotNull(mutations);
        assertEquals(mutations.size(), 2);
        assertEquals(mutations.get(0).getItems().size(), 2);
        boolean verifiedIntBytes = false;
        for(MockMutation.MockColQualVal item : mutations.get(0).getItems()){
            if(item.getQual().equals("age")){
                byte[] value = item.getValue();
                byte[] testInt = "13".getBytes();
                assertEquals(testInt, value);
                verifiedIntBytes = true;
            }
        }
        assertTrue(verifiedIntBytes);
    }

    @Test
    public void missingColFam() {

        ImmutableList<CellGroup<IntValueCell>> missingColFamGroup;
        ImmutableList.Builder<CellGroup<IntValueCell>> builder3 = ImmutableList.builder();
        CellGroup<IntValueCell> row5 = new CellGroup<IntValueCell>("row5");
        row5.addCell(new IntValueCell("l",1));
        builder3.add(row5);
        missingColFamGroup = builder3.build();

        assertEquals(missingColFamGroup.size(), 1);
        assertEquals(missingColFamGroup.get(0).getInternalList().size(), 1);
        DBRecordWriter<MockMutation> writer = new MockDBCellValueWriter();
        CommonParameters parameters = new CommonParameters.Builder().build();

        try {
            writer.write(missingColFamGroup, parameters, new DBItemTransformer<MockMutation, IntValueCell>() {
                public List<MockMutation> apply(CellGroup<IntValueCell> cellGroup)
                        throws CellExtractorException {
                    ImmutableList.Builder<MockMutation> list = ImmutableList.builder();
                    MockMutation result = new MockMutation(cellGroup.getTag());
                    for(IntValueCell cell : cellGroup.getInternalList()) {
                        String qual = null;
                        qual = CellReflector.getLabelAsString(cell);
                        byte[] valueBytes = CellReflector.getValueBytesIfPrimative(cell);
                        String colFam;
                        colFam = CellReflector.getColFam(cell);
                        result.addItem(new MockMutation.MockColQualVal(colFam, qual, valueBytes));
                    }
                    list.add(result);
                    return list.build();
                }
            });
            fail("tried to write a group with missing col fam field");
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.MISSING_COLFAM_ON_WRITE);
            assertTrue(e.getMessage().contains("no column family annotated for given cell type"));
        }
    }

    @Test
    public void nullColFam() {

        ImmutableList<CellGroup<StringValueCell>> nullColFamGroup;
        ImmutableList.Builder<CellGroup<StringValueCell>> builder3 = ImmutableList.builder();
        CellGroup<StringValueCell> row5 = new CellGroup<StringValueCell>("row5");
        row5.addCell(new StringValueCell("l","2"));
        builder3.add(row5);
        nullColFamGroup = builder3.build();

        assertEquals(nullColFamGroup.size(), 1);
        assertEquals(nullColFamGroup.get(0).getInternalList().size(), 1);
        DBRecordWriter<MockMutation> writer = new MockDBCellValueWriter();
        CommonParameters parameters = new CommonParameters.Builder().build();

        ImmutableList<MockMutation> mutations = null;
        try {
            writer.write(nullColFamGroup, parameters, stringTransformer);
            fail("tried to write a group with null col fam field");
        } catch (CellExtractorException e) {
            assertEquals(e.getType(), ErrorType.NULL_FIELD);
            assertTrue(e.getMessage().contains("Column family value is null"));
        }

    }

}
