package com.mvvm.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Formatter;

public class OtpGenerator {
    private static final OtpGenerator ourInstance = new OtpGenerator();
    // Used to load the 'native-lib' library on application startup.


    public static OtpGenerator getInstance() {
        return ourInstance;
    }

    private OtpGenerator() {
    }

    public String generateOTP(String key) {

        byte[] otpByte = generateOtp(key);
        byte[] byteRange = Arrays.copyOfRange(otpByte, 10, 14);

        String hexValue = truncateWhenUTF8("0x" + toHexString(byteRange), 31);

        Log.d("OTP HEx", hexValue);

        int otp = getDecimal(hexValue);
        String fullOtp = String.valueOf(otp);
        String sOtpGenereted = fullOtp.substring(fullOtp.length() - 6, fullOtp.length());

        Log.d("OTP OTP", sOtpGenereted);

        return sOtpGenereted;

    }

    private static int getDecimal(String hex) {
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16 * val + d;
        }
        return val;
    }

    private String truncateWhenUTF8(String s, int maxBytes) {
        int b = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int skip = 0;
            int more;
            if (c <= 0x007f) {
                more = 1;
            } else if (c <= 0x07FF) {
                more = 2;
            } else if (c <= 0xd7ff) {
                more = 3;
            } else if (c <= 0xDFFF) {
                more = 4;
                skip = 1;
            } else {
                more = 3;
            }
            if (b + more > maxBytes) {
                return s.substring(0, i);
            }
            b += more;
            i += skip;
        }
        return s;
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }

    private native byte[] generateOtp(String key);
}
