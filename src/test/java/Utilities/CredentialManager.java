package Utilities;

import models.LoginCredentials;

public class CredentialManager {

    public static LoginCredentials getCredentials(String loginUser) {

        if ("DEFAULT_USER".equalsIgnoreCase(loginUser)) {

            String mobileNumber = System.getenv("TEST_DEFAULT_USER_MOBILE");
            String password = System.getenv("TEST_DEFAULT_USER_PASSWORD");

            validateCredential(
                    "TEST_DEFAULT_USER_MOBILE",
                    mobileNumber
            );

            validateCredential(
                    "TEST_DEFAULT_USER_PASSWORD",
                    password
            );

            return new LoginCredentials(
                    mobileNumber,
                    password
            );
        }

        throw new IllegalArgumentException(
                "Unsupported login user: " + loginUser
        );
    }

    private static void validateCredential(
            String variableName,
            String value) {

        if (value == null || value.trim().isEmpty()) {

            throw new IllegalStateException(
                    "Required credential is not configured: "
                            + variableName
            );
        }
    }
}