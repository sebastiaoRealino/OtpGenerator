package com.mvvm.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class OtpGenerator {
    private static final OtpGenerator ourInstance = new OtpGenerator();

    public static OtpGenerator getInstance() {
        return ourInstance;
    }

    private OtpGenerator() {

    }

    public Observable generateOtpInterval(String key) {
        return Observable.interval(0, 10, TimeUnit.SECONDS)
                .map(emitter -> generateOTP(key));
    }

    private String generateOTP(String key) {
        byte[] otpByte = generateOtp(key);
        byte[] byteRange = Arrays.copyOfRange(otpByte, 10, 14);
        return changeSignalBit(byteRange);
    }

    private String changeSignalBit(byte[] otpBytes) {
        String bitString = hexToBin(toHexString(otpBytes));
        String changeSignalBit = "0" + bitString.substring(1);
        int decBit = Integer.parseInt(changeSignalBit, 2);
        String fullOtp = Integer.toString(decBit);

        return fullOtp.substring(fullOtp.length() - 6, fullOtp.length());
    }

    private String hexToBin(String s) {
        return new BigInteger(s, 16).toString(2);
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
