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
    public Zip() {
    }

    public static byte[] zipBytes(byte[] input) {
        try {
            if(input != null && input.length != 0) {
                ByteArrayOutputStream e = new ByteArrayOutputStream();
                DeflaterOutputStream dos = new DeflaterOutputStream(e);
                dos.write(input);
                e.close();
                dos.close();
                return e.toByteArray();
            } else {
                return null;
            }
        } catch (IOException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static byte[] unzipBytes(byte[] input) {
        try {
            if(input != null && input.length != 0) {
                InflaterInputStream e = new InflaterInputStream(new ByteArrayInputStream(input));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                int length;
                while((length = e.read(buf)) > 0) {
                    bos.write(buf, 0, length);
                }

                e.close();
                bos.close();
                return bos.toByteArray();
            } else {
                return null;
            }
        } catch (IOException var5) {
            var5.printStackTrace();
            return null;
        }
    }
}
