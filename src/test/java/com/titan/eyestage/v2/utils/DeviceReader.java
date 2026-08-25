package com.titan.eyestage.v2.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// Reads the device + login matrix from TestData/DeviceConfig.xlsx so both the
// number of parallel devices and which credentials log in on each one are
// controlled by the sheet, not hardcoded in Java/XML.
public class DeviceReader {

	private static final String SHEET_NAME = "Devices";

	// Returns one row per device marked Execute=Y, in the order LoginTest.Steps
	// expects its params: {mobileNumber, password, deviceName, osVersion}
	public static Object[][] getDevices() {

		String path = Paths.get(
				System.getProperty("user.dir"),
				"TestData",
				"DeviceConfig.xlsx")
				.toString();

		List<Object[]> devices = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			DataFormatter formatter = new DataFormatter();

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);

			if (sheet == null) {
				throw new RuntimeException(
						"Sheet not found in DeviceConfig.xlsx: " + SHEET_NAME);
			}

			Row headerRow = sheet.getRow(0);

			int deviceNameCol = findColumn(headerRow, formatter, "DeviceName");
			int osVersionCol = findColumn(headerRow, formatter, "OSVersion");
			int mobileNumberCol = findColumn(headerRow, formatter, "MobileNumber");
			int passwordCol = findColumn(headerRow, formatter, "Password");
			int executeCol = findColumn(headerRow, formatter, "Execute");

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {

				Row row = sheet.getRow(rowNum);

				if (row == null) {
					continue;
				}

				String deviceName = formatter.formatCellValue(row.getCell(deviceNameCol)).trim();

				if (deviceName.isEmpty()) {
					continue;
				}

				String executeFlag = formatter.formatCellValue(row.getCell(executeCol)).trim();

				if (!executeFlag.isEmpty() && !executeFlag.equalsIgnoreCase("Y")) {
					continue;
				}

				String osVersion = formatter.formatCellValue(row.getCell(osVersionCol)).trim();
				String mobileNumber = formatter.formatCellValue(row.getCell(mobileNumberCol)).trim();
				String password = formatter.formatCellValue(row.getCell(passwordCol)).trim();

				if (mobileNumber.isEmpty() || password.isEmpty()) {
					throw new RuntimeException(
							"MobileNumber/Password missing for device row: " + deviceName);
				}

				devices.add(new Object[] { mobileNumber, password, deviceName, osVersion });
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read DeviceConfig.xlsx", e);
		}

		if (devices.isEmpty()) {
			throw new RuntimeException(
					"No devices with Execute=Y found in TestData/DeviceConfig.xlsx");
		}

		return devices.toArray(new Object[0][]);
	}

	private static int findColumn(Row headerRow, DataFormatter formatter, String columnName) {

		for (Cell cell : headerRow) {

			if (formatter.formatCellValue(cell).equalsIgnoreCase(columnName)) {
				return cell.getColumnIndex();
			}
		}

		throw new RuntimeException("Column not found in DeviceConfig.xlsx: " + columnName);
	}
}
