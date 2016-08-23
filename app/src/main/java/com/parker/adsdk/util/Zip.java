package com.parker.adsdk.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by parker on 2016/8/23.completed
 */
public class Zip {
    public static byte[] deflate(byte[] buf) {
        byte[] result = null;
        if(buf != null) {
            try {
                if(buf.length == 0) {
                    return result;
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DeflaterOutputStream dos = new DeflaterOutputStream(((OutputStream)baos));
                dos.write(buf);
                baos.close();
                dos.close();
                result = baos.toByteArray();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static byte[] inflate(byte[] buf) {
        byte[] result = null;
        if(buf == null) {
            return result;
        }

        try {
            if(buf.length != 0) {
                InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(buf));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] tmpBuf = new byte[1024];
                while(true) {
                    int len = iis.read(tmpBuf);
                    if(len <= 0) {
                        break;
                    }

                    baos.write(tmpBuf, 0, len);
                }

                iis.close();
                baos.close();
                result = baos.toByteArray();
            }

            return result;
        }
        catch(IOException e) {
            e.printStackTrace();
            return result;
        }
    }
}
