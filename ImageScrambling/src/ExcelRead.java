import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;

import jxl.*;
import jxl.read.biff.BiffException;

public class ExcelRead {

    private static final String EXCEL_FILE_LOCATION = "MyFirstExcel.xls";

    public static void main(String[] args) throws ParseException {

        Workbook workbook = null;
        double sumTot = 0;
        double media = 0;
        try {

            workbook = Workbook.getWorkbook(new File(EXCEL_FILE_LOCATION));

            Sheet sheet = workbook.getSheet(0);
            
            for(int i=600;i<620;i++) {														//RANGE DI VALORI NELLE CELLE NEL FILE EXCEL
            	Cell cell3 = sheet.getCell(1, i);
                double l = DecimalFormat.getNumberInstance().parse(cell3.getContents()).doubleValue();
                
                sumTot += l;
            }
            media = sumTot/20;
            String ss = String.valueOf(media);
            System.out.println("TOTALEEE E' "+ss.replace('.', ','));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } finally {

            if (workbook != null) {
                workbook.close();
            }

        }

    }

}