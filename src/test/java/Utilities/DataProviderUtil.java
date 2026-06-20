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
}
