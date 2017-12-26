package com.maogu.htclibrary.encrypt;

import com.maogu.htclibrary.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * MD5加密帮助类
 *
 * @author Zou.sq
 * @version 1.0
 */
public class Md5Tool {

    public static String md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte digest : b) {
                i = digest;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file 文件
     * @return 文件的MD5值
     */
    public static String getFileMD5(File file) {
        if (!file.isFile() || !file.exists()) {
            return null;
        }
        MessageDigest digest;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtil.closeStream(in);
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        String result = bigInt.toString(16);
        while (result.length() < 32) {
            result = "0" + result;
        }
        return result;
    }
}
