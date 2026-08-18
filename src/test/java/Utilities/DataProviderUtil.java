package Utilities;

import org.testng.annotations.DataProvider;

public class DataProviderUtil {
	
	@DataProvider(name="userDatails")
	
	public Object[][] testData	()
	{
		return new Object[][]
		{
			
			{"8698294937","254265"},
			
		};
	}

	
@DataProvider(name="Categories")	
	
	public Object[][] testData1	()
	{
		return new Object[][]
		{
			
			{"Sunglasses"},
			{"Contact Lenses"},
			{"Eyeglasses"}
			
		};
	}

@DataProvider(name="findMYFit")
  public Object[][] testdata2()
  {  return new Object[][]
		  {
		  {"Eyeglasses","35","15","125"},
		  {"Sunglasses","36","20","130"}
		  };
  }


//Excel DataProvider
@DataProvider(name = "purchaseData")
public Object[][] purchaseData() {

    ExcelReader excel = new ExcelReader();

    int rowCount = excel.getRowCount("TestData");

    Object[][] data = new Object[rowCount][4];

    for (int row = 1; row <= rowCount; row++) {

        data[row - 1][0] =
                excel.getCellData("TestData", row, "TestCaseID");

        data[row - 1][1] =
                CartDataMapper.getProducts(row);

        data[row - 1][2] =
                CartDataMapper.getPaymentMethod(row);

        data[row - 1][3] =
                CartDataMapper.getLoginUser(row);
    }

    excel.closeWorkbook();

    return data;
}
}
