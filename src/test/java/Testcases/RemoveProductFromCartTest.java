package Testcases;

import java.io.IOException;

import org.testng.annotations.Test;

import com.titan.eyestage.v2.Base;
import com.titan.eyestage.v2.pom.LoginElementsNew;

public class RemoveProductFromCartTest extends Base{
	
@Test
	public void testRemoveProductFromCart() throws InterruptedException, IOException {
		LoginElementsNew login = new LoginElementsNew(driver());
		login.testexecution("8963666666", "254265");
	}

}
