package com.bokecc.sdk.mobile.drm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Created by Administrator on 2017/12/8.
 */

public class DecryptVideo {
    /**
     * 解密
     *
     * @param src      byte[]
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(key);
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }

    public static void parseLocal(DataOutputStream dataOutput, File file, byte[] token) throws Exception {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        randomAccessFile.getFilePointer();
        long sourceFileSize = randomAccessFile.length();
        InputStream in = null;

        try {
            // 根据文件创建文件的输入流
            in = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] pcmHeadByte = new byte[151];
        in.read(pcmHeadByte);

        long decryptLen = 0L;
        byte[] decryptBuffer = new byte[2048];
        long[] len = {8184, 8184, 8184, 8184, 8184, 8184};
        int len$ = len.length;

        for (int rangeSize = 0; rangeSize < len$; ++rangeSize) {
            long len1 = len[rangeSize];
            byte[] encryptBytes = new byte[(int) len1];
            in.read(encryptBytes);

            Object decryptBytes = null;
            byte[] var20;
            if (true/*this.currentVersion == 6*/) {
                var20 = decrypt(encryptBytes, token);
            }

            int bytesLen = var20.length;
            byte[] tempBuffer = new byte[(int) (decryptLen + (long) bytesLen)];
            if (decryptLen > 0L) {
                System.arraycopy(decryptBuffer, 0, tempBuffer, 0, (int) decryptLen);
            }

            System.arraycopy(var20, 0, tempBuffer, (int) decryptLen, bytesLen);
            decryptBuffer = tempBuffer;
            decryptLen += (long) bytesLen;
        }

        long var18 = 0L;
        dataOutput.write(decryptBuffer);
        var18 = sourceFileSize - decryptLen;
        parseUnEncryptData(dataOutput, var18, in);

        try {
            // 关闭输入流
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void parseUnEncryptData(DataOutputStream dataOutput, long len, InputStream in) throws IOException {
        byte[] buffer = new byte[2048];
        long offset = 0L;
        boolean stop = false;

        int readLen1;
        while (!stop && (readLen1 = in.read(buffer)) != -1) {
            offset += (long) readLen1;
            int bufferLen = readLen1;
            if (offset >= len) {
                stop = true;
                bufferLen = (int) ((long) readLen1 - (offset - len));
                buffer = Arrays.copyOf(buffer, bufferLen);
            }

            dataOutput.write(buffer, 0, bufferLen);
            dataOutput.flush();
        }
    }
}
