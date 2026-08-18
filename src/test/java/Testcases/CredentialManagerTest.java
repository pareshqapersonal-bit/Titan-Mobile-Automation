package Testcases;

import org.testng.annotations.Test;

import Utilities.CredentialManager;
import models.LoginCredentials;

public class CredentialManagerTest {

    @Test
    public void verifyDefaultUserCredentials() {

        LoginCredentials credentials =
                CredentialManager.getCredentials("DEFAULT_USER");

        System.out.println(
                "Mobile Loaded = "
                        + (credentials.getMobileNumber() != null
                        && !credentials.getMobileNumber().isEmpty()));

        System.out.println(
                "Password Loaded = "
                        + (credentials.getPassword() != null
                        && !credentials.getPassword().isEmpty()));
    }
}