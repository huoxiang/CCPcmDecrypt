package com.bokecc.sdk.mobile.drm;

import java.security.MessageDigest;

public class DESUtil {
    private static String encryptToken = "BokeCC";

    public static String getDecryptString(String token) {
        String str = getString(token);
        return encrypt(str);
    }

    private static String encrypt(String result) {
        String mixedStr = result + "|" + encryptToken;
        String md5Result = MD5(mixedStr);
        String encryptResult = null;
        if(md5Result != null) {
            encryptResult = md5Result.substring(0, 8);
        }

        return encryptResult;
    }

    private static String MD5(String s) {
        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

        try {
            byte[] e = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(e);
            byte[] md = mdInst.digest();
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;

            for(int i = 0; i < j; ++i) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 15];
                str[k++] = hexDigits[byte0 & 15];
            }

            return new String(str);
        } catch (Exception var10) {
            var10.printStackTrace();
            return null;
        }
    }

    private static native String getString(String var0);
    
    static {
        try {
            System.loadLibrary("dwmedia");
        } catch (Error var1) {
            ;
        }

        encryptToken = "BokeCC";
    }
}
