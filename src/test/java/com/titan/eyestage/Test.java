package com.titan.eyestage;

import Utilities.ConfigReader;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ConfigReader config = new ConfigReader();

		System.out.println(config.getProperty("deviceName"));
		System.out.println(config.getProperty("mobileNumber"));
	}

}
