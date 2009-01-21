package ubic.GEOMMTx.evaluation;

import org.apache.poi.hssf.usermodel.HSSFSheet;

public interface SpreadSheetFilter {
    boolean accept( HSSFSheet sheet, int row );
}
