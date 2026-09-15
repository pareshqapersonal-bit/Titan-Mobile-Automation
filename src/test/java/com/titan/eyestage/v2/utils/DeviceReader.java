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

	// Returns every login available for one device (matched by DeviceName, Execute=Y): the
	// primary MobileNumber/Password plus MobileNumber2/Password2 when that row has them
	// filled in. Exists so a single-device parallel run (ParallelRowsPurchaseTest, which
	// splits one device's rows across threads) can hand each thread a different account
	// instead of every thread logging in with the same credentials at once. MobileNumber2/
	// Password2 are optional columns - most device rows won't have them, and getDevices()
	// (used by the device-parallel and login suites) deliberately ignores them so those
	// suites keep seeing one row per device.
	public static List<String[]> getCredentialPool(String deviceName) {

		String path = Paths.get(
				System.getProperty("user.dir"),
				"TestData",
				"DeviceConfig.xlsx")
				.toString();

		List<String[]> credentials = new ArrayList<>();

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
			int mobileNumberCol = findColumn(headerRow, formatter, "MobileNumber");
			int passwordCol = findColumn(headerRow, formatter, "Password");
			int executeCol = findColumn(headerRow, formatter, "Execute");
			int mobileNumber2Col = findColumnOptional(headerRow, formatter, "MobileNumber2");
			int password2Col = findColumnOptional(headerRow, formatter, "Password2");

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {

				Row row = sheet.getRow(rowNum);

				if (row == null) {
					continue;
				}

				String rowDeviceName = formatter.formatCellValue(row.getCell(deviceNameCol)).trim();

				if (!deviceName.equalsIgnoreCase(rowDeviceName)) {
					continue;
				}

				String executeFlag = formatter.formatCellValue(row.getCell(executeCol)).trim();

				if (!executeFlag.isEmpty() && !executeFlag.equalsIgnoreCase("Y")) {
					continue;
				}

				String mobileNumber = formatter.formatCellValue(row.getCell(mobileNumberCol)).trim();
				String password = formatter.formatCellValue(row.getCell(passwordCol)).trim();

				credentials.add(new String[] { mobileNumber, password });

				if (mobileNumber2Col >= 0 && password2Col >= 0) {

					String mobileNumber2 = formatter.formatCellValue(row.getCell(mobileNumber2Col)).trim();
					String password2 = formatter.formatCellValue(row.getCell(password2Col)).trim();

					if (!mobileNumber2.isEmpty() && !password2.isEmpty()) {
						credentials.add(new String[] { mobileNumber2, password2 });
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read DeviceConfig.xlsx", e);
		}

		if (credentials.isEmpty()) {
			throw new RuntimeException(
					"No Execute=Y row with credentials found for device '" + deviceName
							+ "' in TestData/DeviceConfig.xlsx");
		}

		return credentials;
	}

	private static int findColumn(Row headerRow, DataFormatter formatter, String columnName) {

		int col = findColumnOptional(headerRow, formatter, columnName);

		if (col < 0) {
			throw new RuntimeException("Column not found in DeviceConfig.xlsx: " + columnName);
		}

		return col;
	}

	private static int findColumnOptional(Row headerRow, DataFormatter formatter, String columnName) {

		for (Cell cell : headerRow) {

			if (formatter.formatCellValue(cell).equalsIgnoreCase(columnName)) {
				return cell.getColumnIndex();
			}
		}

		return -1;
	}
}
