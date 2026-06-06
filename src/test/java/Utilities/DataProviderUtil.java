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

}
