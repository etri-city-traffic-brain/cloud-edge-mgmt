package com.innogrid.uniq.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Base64;

public class AES256Util {
    private String iv;
    private Key keySpec;

    public void setKey(String key) throws UnsupportedEncodingException {

        this.iv = key.substring(0, 16);
        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;
    }

    public String encrypt(String str) {
//        try {
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
//            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
//            String enStr = new String(Base64.getEncoder().encode(encrypted));
//            return enStr;
//        } catch (GeneralSecurityException gse) {
//
//        } catch (UnsupportedEncodingException uee) {
//
//        }

        return str;
    }

    public String decrypt(String str) {
//        try {
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
//            byte[] byteStr = Base64.getDecoder().decode(str.getBytes());
//            return new String(c.doFinal(byteStr), "UTF-8");
//        } catch (GeneralSecurityException gse) {
//
//        } catch (UnsupportedEncodingException uee) {
//
//        }

        return str;
    }
}
