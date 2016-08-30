package com.parker.adsdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;

import com.parker.adsdk.enc.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by parker on 2016/8/26.completed
 */


public class NetworkRequest {
    private static final String end = "\r\n";
    private static final String twoHyphens = "--";
    private static final String boundary = "******";
    private static final int BLOCK_SIZE = 4096;
    public static final String UPLOAD_FILE_NAME = "file";
    protected Context context;
    protected URL url;
    protected ArrayList<NetworkRequest.UploadPart> partList;
    protected boolean canceled;
    protected int timeout = '\uea60';

    public NetworkRequest(Context context, URL url) {
        this.context = context;
        this.url = url;
        this.partList = new ArrayList();
        this.canceled = false;
    }

    public NetworkRequest addUploadPart(String name, String fileName, byte[] content) {
        this.partList.add(new NetworkRequest.UploadPart(name, fileName, content));
        return this;
    }

    public NetworkRequest addUploadPartWithDataZip(String name, String fileName, byte[] content) throws Exception {
        this.partList.add(new NetworkRequest.UploadPart(name, fileName, Util.zipAndEncrypt(content)));
        return this;
    }

    public NetworkRequest addUploadPart(String name, String fileName, String fullFilePath) {
        this.partList.add(new NetworkRequest.UploadPart(name, fileName, fullFilePath));
        return this;
    }

    public NetworkRequest setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public byte[] doRequestWithUnzipResult() throws Exception {
        return Util.decryptAndUnzip(this.doRequest(true));
    }

    public byte[] doRequest() throws NetworkRequest.ConnectionException {
        return this.doRequest(false);
    }

    public byte[] doRequest(boolean compress) throws NetworkRequest.ConnectionException {
        DataOutputStream dos = null;
        InputStream is = null;
        byte[] result = new byte[0];

        try {
            final HttpURLConnection connection;
            if(isWapNetwork(this.context)) {
                Proxy httpURLConnection = new Proxy(Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
                connection = (HttpURLConnection)this.url.openConnection(httpURLConnection);
            } else {
                connection = (HttpURLConnection)this.url.openConnection();
            }

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("X-Compress", compress?"true":"false");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=******");
            connection.setRequestProperty("User-Agent", getUserAgent());
            connection.setConnectTimeout(this.timeout);
            connection.setReadTimeout(this.timeout);
            final CountDownLatch cdl = new CountDownLatch(1);
            (new Thread(new Runnable() {
                public void run() {
                    try {
                        connection.connect();
                    } catch (Exception var2) {
                        var2.printStackTrace();
                    }

                    cdl.countDown();
                }
            })).start();

            try {
                if(!cdl.await(30L, TimeUnit.SECONDS)) {
                    throw new NetworkRequest.ConnectionException("timeout");
                }
            } catch (Exception var26) {
                return null;
            }

            dos = new DataOutputStream(connection.getOutputStream());

            for(Iterator buffer = this.partList.iterator(); buffer.hasNext(); dos.writeBytes("\r\n")) {
                NetworkRequest.UploadPart stream = (NetworkRequest.UploadPart)buffer.next();
                dos.writeBytes("--******\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"" + stream.name + "\"; filename=\"" + stream.fileName + "\"" + "\r\n");
                dos.writeBytes("Content-Type:application/octet-stream;\r\n\r\n");
                Object size = null;
                long contentLength = 0L;
                if(stream.content != null) {
                    size = new ByteArrayInputStream(stream.content);
                    contentLength = (long)stream.content.length;
                } else if(!TextUtils.isEmpty(stream.fullFilePath) && (new File(stream.fullFilePath)).exists()) {
                    size = new FileInputStream(stream.fullFilePath);
                    contentLength = (new File(stream.fullFilePath)).length();
                }

                if(contentLength > 0L) {
                    byte[] buffer1 = new byte[4096];

                    int size1;
                    while((size1 = ((InputStream)size).read(buffer1)) >= 0) {
                        dos.write(buffer1, 0, size1);
                    }
                }
            }

            dos.writeBytes("--******--\r\n");
            dos.flush();
            is = connection.getInputStream();
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            byte[] buffer2 = new byte[4096];

            int size2;
            while((size2 = is.read(buffer2)) >= 0) {
                stream1.write(buffer2, 0, size2);
            }

            is.close();
            result = stream1.toByteArray();
            return result;
        } catch (ProtocolException var27) {
            throw new NetworkRequest.ConnectionException(var27);
        } catch (FileNotFoundException var28) {
            throw new NetworkRequest.ConnectionException(var28);
        } catch (IOException var29) {
            throw new NetworkRequest.ConnectionException(var29);
        } finally {
            try {
                if(dos != null) {
                    dos.close();
                }

                if(is != null) {
                    is.close();
                }
            } catch (Exception var25) {
                ;
            }

        }
    }

    public static String getUserAgent() {
        String version = VERSION.RELEASE;
        String model = Build.MODEL;
        String display = Build.DISPLAY;
        String userAgent = String.format("Mozilla/5.0 (Linux; Android %1$s; %2$s Build/%3$s) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Safari/535.19", new Object[]{version, model, display});
        return userAgent;
    }

    public static boolean isWapNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if(info.getType() != 1 && info.getType() != 9) {
                String currentAPN = info.getExtraInfo();
                return currentAPN == null?false:currentAPN.equalsIgnoreCase("cmwap") || currentAPN.equalsIgnoreCase("ctwap") || currentAPN.equalsIgnoreCase("3gwap") || currentAPN.equalsIgnoreCase("uniwap");
            } else {
                return false;
            }
        } catch (Exception var4) {
            return false;
        }
    }

    public final class ConnectionException extends Exception {
        public ConnectionException(String detailMessage) {
            super(detailMessage);
        }

        public ConnectionException(Throwable throwable) {
            super(throwable);
        }
    }

    private class UploadPart {
        String name;
        String fileName;
        byte[] content;
        String fullFilePath;

        public UploadPart(String name, String fileName, byte[] content) {
            this.name = name;
            this.fileName = fileName;
            this.content = content;
            this.fullFilePath = null;
        }

        public UploadPart(String name, String fileName, String fullFilePath) {
            this.name = name;
            this.fileName = fileName;
            this.fullFilePath = fullFilePath;
            this.content = null;
        }
    }
}