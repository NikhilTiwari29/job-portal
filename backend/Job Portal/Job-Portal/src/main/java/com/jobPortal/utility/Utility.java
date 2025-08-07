package com.jobPortal.utility;

public class Utility {

    public static String generateOtp(){
        return String.valueOf((int) ((Math.random() * 900000) + 100000));
    }
}
