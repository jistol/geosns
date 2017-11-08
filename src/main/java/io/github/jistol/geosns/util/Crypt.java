package io.github.jistol.geosns.util;


import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.util.Base64;
import java.util.function.Function;

@Slf4j
public class Crypt implements Serializable {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String CHAR_ENCODING = "UTF-8";

    private final SecretKeySpec secretKey;
    private final byte[] iv;

    public Crypt(String key, String iv) throws Exception {
        byte[] keyData = key.getBytes();
        this.secretKey = new SecretKeySpec(keyData, "AES");
        this.iv = iv.getBytes();
    }

    private byte[] encryptAES(String plain) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return c.doFinal(plain.getBytes(CHAR_ENCODING));
    }

    private byte[] decryptAES(byte[] cipher) throws Exception {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        return c.doFinal(cipher);
    }

    public String encrypt(String plain) throws Exception {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encryptAES(plain));
    }

    public String decrypt(String encStr) throws Exception {
        return new String(decryptAES(Base64.getUrlDecoder().decode(encStr)), CHAR_ENCODING);
    }

    public String urlEncrypt(String plain) throws Exception {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(plain.getBytes());
    }

    public String urlDecrypt(String encStr) throws Exception {
        return new String(Base64.getUrlDecoder().decode(encStr), CHAR_ENCODING);
    }

    public String encrypt(String plain, Function<Exception, RuntimeException> exception) {
        try {
            return encrypt(plain);
        } catch (Exception e) {
            throw exception.apply(e);
        }
    }

    public String decrypt(String encStr, Function<Exception, RuntimeException> exception) {
        try {
            return decrypt(encStr);
        } catch (Exception e) {
            throw exception.apply(e);
        }
    }

    public String urlEncrypt(String plain, Function<Exception, RuntimeException> exception) {
        try {
            return urlEncrypt(plain);
        } catch (Exception e) {
            throw exception.apply(e);
        }
    }

    public String urlDecrypt(String encStr, Function<Exception, RuntimeException> exception) {
        try {
            return urlDecrypt(encStr);
        } catch (Exception e) {
            throw exception.apply(e);
        }
    }
}
