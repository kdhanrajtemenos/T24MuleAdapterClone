package com.temenos.adapter.mule.T24outbound.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

import com.temenos.adapter.oracle.BuildConfig;

public class PasswdUtil {

    private static final char[] PASSWORD = BuildConfig.SECRET_KEY.toCharArray();
    // "nTC3yGauHoGp/3.s/P558.KYEcFTrU".toCharArray();

    private static final byte[] SALT = BuildConfig.SECRET_SALT.getBytes();

    /*{
        (byte) 0x1a, (byte) 0xaa, (byte) 0xaa, (byte) 0xa1,
        (byte) 0xcc, (byte) 0xc4, (byte) 0xff, (byte) 0x11,
    };*/
    private static final String ALGO = "PBEWithMD5AndDES";

    public static String encrypt(String text) throws GeneralSecurityException {
        if (text == null || text.isEmpty()) {
            return "";
        }
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGO);
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
        Cipher pbeCipher = Cipher.getInstance(ALGO);
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
        return String.format("{%s}", base64Encode(pbeCipher.doFinal(text.getBytes())));
    }

    private static String base64Encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static String decrypt(String text)  {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (!text.startsWith("{") || !text.endsWith("}")) {
            return text;
        }
        try {
            String pass = text.substring(1, text.length() - 1);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGO);
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(PASSWORD));
            Cipher pbeCipher = Cipher.getInstance(ALGO);
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 20));
            return new String(pbeCipher.doFinal(base64Decode(pass)));
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            return text;
        }
    }

    private static byte[] base64Decode(String property) throws IOException {
        return DatatypeConverter.parseBase64Binary(property);
    }

}
