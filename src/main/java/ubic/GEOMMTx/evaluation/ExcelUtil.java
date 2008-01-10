package ubic.GEOMMTx.evaluation;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class ExcelUtil {
    public static String getValue( HSSFSheet sheet, int row, short col ) {
        if ( sheet.getRow( row ) == null ) return null;
        HSSFCell cell = sheet.getRow( row ).getCell( col );
        if ( cell == null ) {
            return null;
        }
        try {
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING ) return cell.getRichStringCellValue().getString();
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
                // WARNING bad for doubles
                return "" + cell.getNumericCellValue();
            }
        } catch ( Exception e ) {
            System.err.println( "row:" + row + " col:" + col );
            e.printStackTrace();
            System.exit( 0 );
        }
        return "";
    }

    public static void setValue( HSSFSheet sheet, int row, int col, String value ) {
        setValue(sheet, row, (short)col, value);
    }
    public static void setValue( HSSFSheet sheet, int row, short col, String value ) {
        HSSFRow r = sheet.createRow( row );
        HSSFCell c = r.createCell( col );
        c.setCellType( HSSFCell.CELL_TYPE_STRING );
        c.setCellValue(new HSSFRichTextString(value));
    }
}
