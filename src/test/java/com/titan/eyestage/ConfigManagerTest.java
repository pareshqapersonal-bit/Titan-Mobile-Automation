package com.titan.eyestage;

import org.testng.annotations.Test;

import Utilities.ConfigManager;

public class ConfigManagerTest {

    @Test
    public void verifyConfigManager() {

        ConfigManager config = new ConfigManager();

        System.out.println("Environment = "
                + config.getProperty("environment"));

        System.out.println("Execution Mode = "
                + config.getProperty("executionMode"));

        System.out.println("APK = "
                + config.getProperty("apkPath"));

        System.out.println("Device = "
                + config.getProperty("deviceName"));

        System.out.println("App Package = "
                + config.getProperty("appPackage"));

        System.out.println("Appium URL = "
                + config.getProperty("appiumURL"));
        
        System.out.println("BrowserStack Device = "
                + config.getProperty("browserstack.device"));

        System.out.println("BrowserStack OS = "
                + config.getProperty("browserstack.osVersion"));

        System.out.println("BrowserStack App = "
                + config.getProperty("browserstack.app"));

        System.out.println("BrowserStack Project = "
                + config.getProperty("browserstack.project"));
        
        String username =
                config.getProperty("browserstack.username");

        String accessKey =
                config.getProperty("browserstack.accessKey");

        System.out.println(
                "BrowserStack Username Loaded = "
                        + (username != null && !username.isEmpty()));

        System.out.println(
                "BrowserStack Access Key Loaded = "
                        + (accessKey != null && !accessKey.isEmpty()));
    }
}