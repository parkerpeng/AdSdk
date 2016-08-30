package com.parker.adsdk.enc;

import com.parker.adsdk.util.Zip;

import java.io.IOException;
import java.util.zip.DataFormatException;

/**
 * Created by parker on 2016/8/10.complete
 */
public class Util {
    private static final double ENCRYPTION_KEY;

    static {
        ENCRYPTION_KEY = (double)AdBase64.EK + AES.EK;
    }

    public Util() {
    }

    public static byte[] zipAndEncrypt(byte[] input) throws IOException {
        AES aes = new AES();
        aes.setKey(aes.genKey(Long.toHexString(Double.doubleToLongBits(ENCRYPTION_KEY))));
        return aes.RootEncrypt(AdBase64.encode(Zip.zipBytes(input)));
    }

    public static byte[] decryptAndUnzip(byte[] input) throws DataFormatException, IOException {
        AES aes = new AES();
        aes.setKey(aes.genKey(Long.toHexString(Double.doubleToLongBits(ENCRYPTION_KEY))));
        return Zip.unzipBytes(AdBase64.decode(new String(aes.RootDecrypt(input))));
    }
}
