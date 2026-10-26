package com.titan.eyestage.v2;

import java.io.IOException;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.titan.eyestage.v2.Base;
import com.titan.eyestage.v2.pom.LoginElementsNew;
import com.titan.eyestage.v2.pom.RemoveProductsFromCartPAge;
import com.titan.eyestage.v2.utils.TestListener;

@Listeners(TestListener.class)
public class RemoveProductFromCartTest extends Base{
	
@Test
	public void testRemoveProductFromCart() throws InterruptedException, IOException {
		LoginElementsNew login = new LoginElementsNew(driver());
		login.testexecution("8963666666", "254265");
		
		RemoveProductsFromCartPAge removeProduct = new RemoveProductsFromCartPAge(driver());
		removeProduct.removeProductFromCart();
	}

}
