package ubic.GEOMMTx.evaluation;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class ExcelUtil {
    
    public static HSSFSheet getSheetFromFile( String filename, String sheetName ) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem( new FileInputStream( filename ) );
        HSSFWorkbook wb = new HSSFWorkbook( fs );
        return wb.getSheet( sheetName );
    }

    public static String getValue( HSSFSheet sheet, int row, int col ) {
        if ( col > 255 ) {
            throw new RuntimeException( "Column position is over 255" );
        }
        return getValue( sheet, row, ( short ) col );
    }

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
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA ) return cell.getCellFormula();

        } catch ( Exception e ) {
            System.err.println( "row:" + row + " col:" + col );
            e.printStackTrace();
            System.exit( 0 );
        }
        return "";
    }

    public static void setValue( HSSFSheet sheet, int row, int col, String value ) {
        HSSFRow r = sheet.createRow( row );
        HSSFCell c = r.createCell( ( short ) col );
        c.setCellType( HSSFCell.CELL_TYPE_STRING );
        c.setCellValue( new HSSFRichTextString( value ) );
    }

    public static void setFormula( HSSFSheet sheet, int row, int col, String value ) {
        HSSFRow r = sheet.createRow( row );
        HSSFCell c = r.createCell( ( short ) col );
        c.setCellType( HSSFCell.CELL_TYPE_FORMULA );
        c.setCellFormula( value );
    }

    public static void main( String args[] ) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet spreadsheet = workbook.createSheet();
        ExcelUtil.setFormula( spreadsheet, 1, 1, "HYPERLINK(\"x\",\"x\")" );

    }

}
