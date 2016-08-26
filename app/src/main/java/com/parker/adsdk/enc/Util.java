package com.parker.adsdk.enc;

import com.parker.adsdk.util.Zip;

/**
 * Created by parker on 2016/8/10.complete
 */
public class Util {
    public static byte[] encode(byte[] buf) {
        AES aes = new AES();
        aes.setEncryptionKey(aes.encryptKey(Long.toHexString(Double.doubleToLongBits(498569365.5688))));
        return aes.encrypt(AdBase64.encode(Zip.deflate(buf)));
    }

    public static byte[] decode(byte[] buf) {
        AES aes = new AES();
        aes.setEncryptionKey(aes.encryptKey(Long.toHexString(Double.doubleToLongBits(498569365.5688))));
        return Zip.inflate(AdBase64.decode(new String(aes.decrypt(buf))));
    }



}
