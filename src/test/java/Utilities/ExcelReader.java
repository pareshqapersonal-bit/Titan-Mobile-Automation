package Utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

    private XSSFWorkbook workbook;
    private DataFormatter formatter;

    public ExcelReader() {

        try {

            String path = Paths.get(
                    System.getProperty("user.dir"),
                    "TestData",
                    "AddToCart.xlsx")
                    .toString();

            FileInputStream fis = new FileInputStream(path);

            workbook = new XSSFWorkbook(fis);

            formatter = new DataFormatter();

            fis.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public String getCellData(String sheetName, int rowNum, String columnName) {

        XSSFSheet sheet = workbook.getSheet(sheetName);

        Row headerRow = sheet.getRow(0);

        int columnIndex = -1;

        for (Cell cell : headerRow) {

            if (formatter.formatCellValue(cell)
                    .equalsIgnoreCase(columnName)) {

                columnIndex = cell.getColumnIndex();

                break;
            }
        }

        if (columnIndex == -1) {

            throw new RuntimeException(
                    "Column not found : " + columnName);
        }

        Row row = sheet.getRow(rowNum);

        Cell cell = row.getCell(columnIndex);

        return formatter.formatCellValue(cell);
    }

    public int getRowCount(String sheetName) {

        XSSFSheet sheet = workbook.getSheet(sheetName);

        for (int rowNum = sheet.getLastRowNum(); rowNum >= 1; rowNum--) {

            Row row = sheet.getRow(rowNum);

            if (row != null) {

                Cell testCaseCell = row.getCell(0);

                if (testCaseCell != null &&
                    !formatter.formatCellValue(testCaseCell).trim().isEmpty()) {

                    return rowNum;
                }
            }
        }

        return 0;
    }

    public int getColumnCount(String sheetName) {

        Row row = workbook.getSheet(sheetName)
                .getRow(0);

        return row.getLastCellNum();
    }

    public void closeWorkbook() {

        try {

            workbook.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }
}