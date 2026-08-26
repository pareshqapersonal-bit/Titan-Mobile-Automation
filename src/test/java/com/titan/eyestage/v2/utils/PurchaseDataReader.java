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

import com.titan.eyestage.v2.models.CartProduct;

// Reads the purchase-journey product/payment matrix from TestData/PurchaseData-v2.xlsx -
// an exact copy of the original TestData/AddToCart.xlsx (same "TestData" sheet, same
// curated SKU/payment-method arrangement per row), so every row is controlled by the
// sheet, not hardcoded in Java. Devices/logins stay in the separate
// TestData/DeviceConfig.xlsx (see DeviceReader) - PurchaseDataProviderUtil pairs the two
// so every purchase row still runs, spread across whatever devices are configured.
public class PurchaseDataReader {

	private static final String SHEET_NAME = "TestData";

	// Excel column header -> CartPageElements category key
	private static final String[][] CATEGORY_COLUMNS = {
			{ "FrameSKU", "Frame" },
			{ "EyeglassSKU", "Eyeglass" },
			{ "SunglassSKU", "Sunglass" },
			{ "CLSKU", "ContactLens" },
			{ "PoweredSunglassSKU", "PoweredSunglass" },
			{ "ReadingGlassSKU", "ReadingGlass" },
			{ "ComputerGlassSKU", "ComputerGlass" },
			{ "AccessoriesSKU", "Accessories" }
	};

	// Returns one row per purchase case marked Execute=Y:
	// {testCaseId(String), products(List<CartProduct>), paymentMethod(String)}
	public static Object[][] getPurchaseCases() {

		String path = Paths.get(
				System.getProperty("user.dir"),
				"TestData",
				"PurchaseData-v2.xlsx")
				.toString();

		List<Object[]> cases = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(path);
				XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

			DataFormatter formatter = new DataFormatter();

			XSSFSheet sheet = workbook.getSheet(SHEET_NAME);

			if (sheet == null) {
				throw new RuntimeException(
						"Sheet not found in PurchaseData-v2.xlsx: " + SHEET_NAME);
			}

			Row headerRow = sheet.getRow(0);

			int testCaseIdCol = findColumn(headerRow, formatter, "TestCaseID");
			int paymentMethodCol = findColumn(headerRow, formatter, "PaymentMethod");

			// AddToCart.xlsx (the sheet this file is a copy of) has no Execute column,
			// so treat it as optional - present means filter, absent means run every row.
			int executeCol = findColumnOptional(headerRow, formatter, "Execute");

			int[] categoryCols = new int[CATEGORY_COLUMNS.length];

			for (int i = 0; i < CATEGORY_COLUMNS.length; i++) {
				categoryCols[i] = findColumn(headerRow, formatter, CATEGORY_COLUMNS[i][0]);
			}

			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {

				Row row = sheet.getRow(rowNum);

				if (row == null) {
					continue;
				}

				String testCaseId = formatter.formatCellValue(row.getCell(testCaseIdCol)).trim();

				if (testCaseId.isEmpty()) {
					continue;
				}

				if (executeCol >= 0) {

					String executeFlag = formatter.formatCellValue(row.getCell(executeCol)).trim();

					if (!executeFlag.isEmpty() && !executeFlag.equalsIgnoreCase("Y")) {
						continue;
					}
				}

				String paymentMethod = formatter.formatCellValue(row.getCell(paymentMethodCol)).trim();

				if (paymentMethod.isEmpty()) {
					throw new RuntimeException(
							"PaymentMethod missing for purchase row: " + testCaseId);
				}

				List<CartProduct> products = new ArrayList<>();

				for (int i = 0; i < CATEGORY_COLUMNS.length; i++) {

					String sku = formatter.formatCellValue(row.getCell(categoryCols[i])).trim();

					if (!sku.isEmpty()) {
						products.add(new CartProduct(CATEGORY_COLUMNS[i][1], sku));
					}
				}

				if (products.isEmpty()) {
					throw new RuntimeException(
							"No product SKUs found for purchase row: " + testCaseId);
				}

				cases.add(new Object[] { testCaseId, products, paymentMethod });
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read PurchaseData-v2.xlsx", e);
		}

		if (cases.isEmpty()) {
			throw new RuntimeException(
					"No purchase cases with Execute=Y found in TestData/PurchaseData-v2.xlsx");
		}

		return cases.toArray(new Object[0][]);
	}

	private static int findColumn(Row headerRow, DataFormatter formatter, String columnName) {

		int col = findColumnOptional(headerRow, formatter, columnName);

		if (col < 0) {
			throw new RuntimeException("Column not found in PurchaseData-v2.xlsx: " + columnName);
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
