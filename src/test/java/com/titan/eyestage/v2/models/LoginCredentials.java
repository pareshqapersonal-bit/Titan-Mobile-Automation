package com.titan.eyestage.v2.models;

public class LoginCredentials {

    private final String mobileNumber;
    private final String password;

    public LoginCredentials(String mobileNumber, String password) {
        this.mobileNumber = mobileNumber;
        this.password = password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }
}
