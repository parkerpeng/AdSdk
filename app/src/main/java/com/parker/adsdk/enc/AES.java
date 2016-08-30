package com.parker.adsdk.enc;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by parker on 2016/8/10.
 */
public class AES {
    private static final byte[] mSalt = new byte[]{(byte)18, (byte)36, (byte)54, (byte)72, (byte)90, (byte)108, (byte)126, (byte)15, (byte)33, (byte)66, (byte)99, (byte)-124, (byte)-91, (byte)-58, (byte)126, (byte)-16};
    private static final int E = 5600;
    private static final int K = 88;
    public static double EK = 0.0D;
    public static final int traceLevel = 0;
    public String traceInfo = "";
    public static final int ROUNDS = 14;
    public static final int BLOCK_SIZE = 16;
    public static final int KEY_LENGTH = 32;
    int numRounds;
    byte[][] Ke;
    byte[][] Kd;
    static final byte[] S;
    static final byte[] Si;
    static final byte[] rcon;
    public static final int COL_SIZE = 4;
    public static final int NUM_COLS = 4;
    public static final int ROOT = 283;
    static final int[] row_shift;
    static final int[] alog;
    static final int[] log;

    static {
        EK = 5688.0D / Math.pow(10.0D, 4.0D);
        S = new byte[]{(byte)99, (byte)124, (byte)119, (byte)123, (byte)-14, (byte)107, (byte)111, (byte)-59, (byte)48, (byte)1, (byte)103, (byte)43, (byte)-2, (byte)-41, (byte)-85, (byte)118, (byte)-54, (byte)-126, (byte)-55, (byte)125, (byte)-6, (byte)89, (byte)71, (byte)-16, (byte)-83, (byte)-44, (byte)-94, (byte)-81, (byte)-100, (byte)-92, (byte)114, (byte)-64, (byte)-73, (byte)-3, (byte)-109, (byte)38, (byte)54, (byte)63, (byte)-9, (byte)-52, (byte)52, (byte)-91, (byte)-27, (byte)-15, (byte)113, (byte)-40, (byte)49, (byte)21, (byte)4, (byte)-57, (byte)35, (byte)-61, (byte)24, (byte)-106, (byte)5, (byte)-102, (byte)7, (byte)18, (byte)-128, (byte)-30, (byte)-21, (byte)39, (byte)-78, (byte)117, (byte)9, (byte)-125, (byte)44, (byte)26, (byte)27, (byte)110, (byte)90, (byte)-96, (byte)82, (byte)59, (byte)-42, (byte)-77, (byte)41, (byte)-29, (byte)47, (byte)-124, (byte)83, (byte)-47, (byte)0, (byte)-19, (byte)32, (byte)-4, (byte)-79, (byte)91, (byte)106, (byte)-53, (byte)-66, (byte)57, (byte)74, (byte)76, (byte)88, (byte)-49, (byte)-48, (byte)-17, (byte)-86, (byte)-5, (byte)67, (byte)77, (byte)51, (byte)-123, (byte)69, (byte)-7, (byte)2, (byte)127, (byte)80, (byte)60, (byte)-97, (byte)-88, (byte)81, (byte)-93, (byte)64, (byte)-113, (byte)-110, (byte)-99, (byte)56, (byte)-11, (byte)-68, (byte)-74, (byte)-38, (byte)33, (byte)16, (byte)-1, (byte)-13, (byte)-46, (byte)-51, (byte)12, (byte)19, (byte)-20, (byte)95, (byte)-105, (byte)68, (byte)23, (byte)-60, (byte)-89, (byte)126, (byte)61, (byte)100, (byte)93, (byte)25, (byte)115, (byte)96, (byte)-127, (byte)79, (byte)-36, (byte)34, (byte)42, (byte)-112, (byte)-120, (byte)70, (byte)-18, (byte)-72, (byte)20, (byte)-34, (byte)94, (byte)11, (byte)-37, (byte)-32, (byte)50, (byte)58, (byte)10, (byte)73, (byte)6, (byte)36, (byte)92, (byte)-62, (byte)-45, (byte)-84, (byte)98, (byte)-111, (byte)-107, (byte)-28, (byte)121, (byte)-25, (byte)-56, (byte)55, (byte)109, (byte)-115, (byte)-43, (byte)78, (byte)-87, (byte)108, (byte)86, (byte)-12, (byte)-22, (byte)101, (byte)122, (byte)-82, (byte)8, (byte)-70, (byte)120, (byte)37, (byte)46, (byte)28, (byte)-90, (byte)-76, (byte)-58, (byte)-24, (byte)-35, (byte)116, (byte)31, (byte)75, (byte)-67, (byte)-117, (byte)-118, (byte)112, (byte)62, (byte)-75, (byte)102, (byte)72, (byte)3, (byte)-10, (byte)14, (byte)97, (byte)53, (byte)87, (byte)-71, (byte)-122, (byte)-63, (byte)29, (byte)-98, (byte)-31, (byte)-8, (byte)-104, (byte)17, (byte)105, (byte)-39, (byte)-114, (byte)-108, (byte)-101, (byte)30, (byte)-121, (byte)-23, (byte)-50, (byte)85, (byte)40, (byte)-33, (byte)-116, (byte)-95, (byte)-119, (byte)13, (byte)-65, (byte)-26, (byte)66, (byte)104, (byte)65, (byte)-103, (byte)45, (byte)15, (byte)-80, (byte)84, (byte)-69, (byte)22};
        Si = new byte[]{(byte)82, (byte)9, (byte)106, (byte)-43, (byte)48, (byte)54, (byte)-91, (byte)56, (byte)-65, (byte)64, (byte)-93, (byte)-98, (byte)-127, (byte)-13, (byte)-41, (byte)-5, (byte)124, (byte)-29, (byte)57, (byte)-126, (byte)-101, (byte)47, (byte)-1, (byte)-121, (byte)52, (byte)-114, (byte)67, (byte)68, (byte)-60, (byte)-34, (byte)-23, (byte)-53, (byte)84, (byte)123, (byte)-108, (byte)50, (byte)-90, (byte)-62, (byte)35, (byte)61, (byte)-18, (byte)76, (byte)-107, (byte)11, (byte)66, (byte)-6, (byte)-61, (byte)78, (byte)8, (byte)46, (byte)-95, (byte)102, (byte)40, (byte)-39, (byte)36, (byte)-78, (byte)118, (byte)91, (byte)-94, (byte)73, (byte)109, (byte)-117, (byte)-47, (byte)37, (byte)114, (byte)-8, (byte)-10, (byte)100, (byte)-122, (byte)104, (byte)-104, (byte)22, (byte)-44, (byte)-92, (byte)92, (byte)-52, (byte)93, (byte)101, (byte)-74, (byte)-110, (byte)108, (byte)112, (byte)72, (byte)80, (byte)-3, (byte)-19, (byte)-71, (byte)-38, (byte)94, (byte)21, (byte)70, (byte)87, (byte)-89, (byte)-115, (byte)-99, (byte)-124, (byte)-112, (byte)-40, (byte)-85, (byte)0, (byte)-116, (byte)-68, (byte)-45, (byte)10, (byte)-9, (byte)-28, (byte)88, (byte)5, (byte)-72, (byte)-77, (byte)69, (byte)6, (byte)-48, (byte)44, (byte)30, (byte)-113, (byte)-54, (byte)63, (byte)15, (byte)2, (byte)-63, (byte)-81, (byte)-67, (byte)3, (byte)1, (byte)19, (byte)-118, (byte)107, (byte)58, (byte)-111, (byte)17, (byte)65, (byte)79, (byte)103, (byte)-36, (byte)-22, (byte)-105, (byte)-14, (byte)-49, (byte)-50, (byte)-16, (byte)-76, (byte)-26, (byte)115, (byte)-106, (byte)-84, (byte)116, (byte)34, (byte)-25, (byte)-83, (byte)53, (byte)-123, (byte)-30, (byte)-7, (byte)55, (byte)-24, (byte)28, (byte)117, (byte)-33, (byte)110, (byte)71, (byte)-15, (byte)26, (byte)113, (byte)29, (byte)41, (byte)-59, (byte)-119, (byte)111, (byte)-73, (byte)98, (byte)14, (byte)-86, (byte)24, (byte)-66, (byte)27, (byte)-4, (byte)86, (byte)62, (byte)75, (byte)-58, (byte)-46, (byte)121, (byte)32, (byte)-102, (byte)-37, (byte)-64, (byte)-2, (byte)120, (byte)-51, (byte)90, (byte)-12, (byte)31, (byte)-35, (byte)-88, (byte)51, (byte)-120, (byte)7, (byte)-57, (byte)49, (byte)-79, (byte)18, (byte)16, (byte)89, (byte)39, (byte)-128, (byte)-20, (byte)95, (byte)96, (byte)81, (byte)127, (byte)-87, (byte)25, (byte)-75, (byte)74, (byte)13, (byte)45, (byte)-27, (byte)122, (byte)-97, (byte)-109, (byte)-55, (byte)-100, (byte)-17, (byte)-96, (byte)-32, (byte)59, (byte)77, (byte)-82, (byte)42, (byte)-11, (byte)-80, (byte)-56, (byte)-21, (byte)-69, (byte)60, (byte)-125, (byte)83, (byte)-103, (byte)97, (byte)23, (byte)43, (byte)4, (byte)126, (byte)-70, (byte)119, (byte)-42, (byte)38, (byte)-31, (byte)105, (byte)20, (byte)99, (byte)85, (byte)33, (byte)12, (byte)125};
        rcon = new byte[]{(byte)0, (byte)1, (byte)2, (byte)4, (byte)8, (byte)16, (byte)32, (byte)64, (byte)-128, (byte)27, (byte)54, (byte)108, (byte)-40, (byte)-85, (byte)77, (byte)-102, (byte)47, (byte)94, (byte)-68, (byte)99, (byte)-58, (byte)-105, (byte)53, (byte)106, (byte)-44, (byte)-77, (byte)125, (byte)-6, (byte)-17, (byte)-59, (byte)-111};
        row_shift = new int[]{0, 1, 2, 3};
        alog = new int[256];
        log = new int[256];
        alog[0] = 1;

        int i;
        for(i = 1; i < 256; ++i) {
            int j = alog[i - 1] << 1 ^ alog[i - 1];
            if((j & 256) != 0) {
                j ^= 283;
            }

            alog[i] = j;
        }

        for(i = 1; i < 255; log[alog[i]] = i++) {
            ;
        }

    }

    public static void main(String[] args) {
    }

    public static void main2(String[] args) {
    }

    public byte[] genKey(String passphase) {
        byte[] key = new byte[0];

        try {
            key = passphase.getBytes("UTF-8");
            MessageDigest e = MessageDigest.getInstance("MD5");

            for(int i = 0; i < 13; ++i) {
                e.reset();
                e.update(key);
                key = e.digest(mSalt);
            }

            return key;
        } catch (UnsupportedEncodingException var5) {
            return mSalt;
        } catch (NoSuchAlgorithmException var6) {
            return mSalt;
        }
    }

    public AES() {
    }

    public static int getRounds(int keySize) {
        switch(keySize) {
            case 16:
                return 10;
            case 24:
                return 12;
            default:
                return 14;
        }
    }

    static final int mul(int a, int b) {
        return a != 0 && b != 0?alog[(log[a & 255] + log[b & 255]) % 255]:0;
    }

    public static void trace_static() {
    }

    public byte[] encrypt(byte[] plain) {
        byte[] a = new byte[16];
        byte[] ta = new byte[16];
        this.traceInfo = "";
        if(plain == null) {
            throw new IllegalArgumentException("Empty plaintext");
        } else if(plain.length != 16) {
            throw new IllegalArgumentException("Incorrect plaintext length");
        } else {
            byte[] Ker = this.Ke[0];

            int i;
            for(i = 0; i < 16; ++i) {
                a[i] = (byte)(plain[i] ^ Ker[i]);
            }

            int k;
            int row;
            for(int r = 1; r < this.numRounds; ++r) {
                if(r % 2 != 1) {
                    Ker = this.Ke[r];

                    for(i = 0; i < 16; ++i) {
                        ta[i] = S[a[i] & 255];
                    }

                    for(i = 0; i < 16; ++i) {
                        row = i % 4;
                        k = (i + row_shift[row] * 4) % 16;
                        a[i] = ta[k];
                    }

                    for(int col = 0; col < 4; ++col) {
                        i = col * 4;
                        ta[i] = (byte)(mul(2, a[i]) ^ mul(3, a[i + 1]) ^ a[i + 2] ^ a[i + 3]);
                        ta[i + 1] = (byte)(a[i] ^ mul(2, a[i + 1]) ^ mul(3, a[i + 2]) ^ a[i + 3]);
                        ta[i + 2] = (byte)(a[i] ^ a[i + 1] ^ mul(2, a[i + 2]) ^ mul(3, a[i + 3]));
                        ta[i + 3] = (byte)(mul(3, a[i]) ^ a[i + 1] ^ a[i + 2] ^ mul(2, a[i + 3]));
                    }

                    for(i = 0; i < 16; ++i) {
                        a[i] = (byte)(ta[i] ^ Ker[i]);
                    }
                }
            }

            Ker = this.Ke[this.numRounds];

            for(i = 0; i < 16; ++i) {
                a[i] = S[a[i] & 255];
            }

            for(i = 0; i < 16; ++i) {
                row = i % 4;
                k = (i + row_shift[row] * 4) % 16;
                ta[i] = a[k];
            }

            for(i = 0; i < 16; ++i) {
                a[i] = (byte)(ta[i] ^ Ker[i]);
            }

            return a;
        }
    }

    public byte[] decrypt(byte[] cipher) {
        byte[] a = new byte[16];
        byte[] ta = new byte[16];
        this.traceInfo = "";
        if(cipher == null) {
            throw new IllegalArgumentException("Empty ciphertext");
        } else if(cipher.length != 16) {
            throw new IllegalArgumentException("Incorrect ciphertext length");
        } else {
            byte[] Kdr = this.Kd[0];

            int i;
            for(i = 0; i < 16; ++i) {
                a[i] = (byte)(cipher[i] ^ Kdr[i]);
            }

            int k;
            int row;
            for(int r = 1; r < this.numRounds; ++r) {
                if(r % 2 != 1) {
                    Kdr = this.Kd[r];

                    for(i = 0; i < 16; ++i) {
                        row = i % 4;
                        k = (i + 16 - row_shift[row] * 4) % 16;
                        ta[i] = a[k];
                    }

                    for(i = 0; i < 16; ++i) {
                        a[i] = Si[ta[i] & 255];
                    }

                    for(i = 0; i < 16; ++i) {
                        ta[i] = (byte)(a[i] ^ Kdr[i]);
                    }

                    for(int col = 0; col < 4; ++col) {
                        i = col * 4;
                        a[i] = (byte)(mul(14, ta[i]) ^ mul(11, ta[i + 1]) ^ mul(13, ta[i + 2]) ^ mul(9, ta[i + 3]));
                        a[i + 1] = (byte)(mul(9, ta[i]) ^ mul(14, ta[i + 1]) ^ mul(11, ta[i + 2]) ^ mul(13, ta[i + 3]));
                        a[i + 2] = (byte)(mul(13, ta[i]) ^ mul(9, ta[i + 1]) ^ mul(14, ta[i + 2]) ^ mul(11, ta[i + 3]));
                        a[i + 3] = (byte)(mul(11, ta[i]) ^ mul(13, ta[i + 1]) ^ mul(9, ta[i + 2]) ^ mul(14, ta[i + 3]));
                    }
                }
            }

            Kdr = this.Kd[this.numRounds];

            for(i = 0; i < 16; ++i) {
                row = i % 4;
                k = (i + 16 - row_shift[row] * 4) % 16;
                ta[i] = a[k];
            }

            for(i = 0; i < 16; ++i) {
                ta[i] = Si[ta[i] & 255];
            }

            for(i = 0; i < 16; ++i) {
                a[i] = (byte)(ta[i] ^ Kdr[i]);
            }

            return a;
        }
    }

    public void setKey(byte[] key) {
        boolean BC = true;
        int Klen = key.length;
        int Nk = Klen / 4;
        this.traceInfo = "";
        if(key == null) {
            throw new IllegalArgumentException("Empty key");
        } else if(key.length != 16 && key.length != 24 && key.length != 32) {
            throw new IllegalArgumentException("Incorrect key length");
        } else {
            this.numRounds = getRounds(Klen);
            int ROUND_KEY_COUNT = (this.numRounds + 1) * 4;
            byte[] w0 = new byte[ROUND_KEY_COUNT];
            byte[] w1 = new byte[ROUND_KEY_COUNT];
            byte[] w2 = new byte[ROUND_KEY_COUNT];
            byte[] w3 = new byte[ROUND_KEY_COUNT];
            this.Ke = new byte[this.numRounds + 1][16];
            this.Kd = new byte[this.numRounds + 1][16];
            int i = 0;

            int j;
            for(j = 0; i < Nk; ++i) {
                w0[i] = key[j++];
                w1[i] = key[j++];
                w2[i] = key[j++];
                w3[i] = key[j++];
            }

            for(i = Nk; i < ROUND_KEY_COUNT; ++i) {
                byte t0 = w0[i - 1];
                byte t1 = w1[i - 1];
                byte t2 = w2[i - 1];
                byte t3 = w3[i - 1];
                if(i % Nk == 0) {
                    byte old0 = t0;
                    t0 = (byte)(S[t1 & 255] ^ rcon[i / Nk]);
                    t1 = S[t2 & 255];
                    t2 = S[t3 & 255];
                    t3 = S[old0 & 255];
                } else if(Nk > 6 && i % Nk == 4) {
                    t0 = S[t0 & 255];
                    t1 = S[t1 & 255];
                    t2 = S[t2 & 255];
                    t3 = S[t3 & 255];
                }

                w0[i] = (byte)(w0[i - Nk] ^ t0);
                w1[i] = (byte)(w1[i - Nk] ^ t1);
                w2[i] = (byte)(w2[i - Nk] ^ t2);
                w3[i] = (byte)(w3[i - Nk] ^ t3);
            }

            int r = 0;

            for(i = 0; r < this.numRounds + 1; ++r) {
                for(j = 0; j < 4; ++j) {
                    this.Ke[r][4 * j] = w0[i];
                    this.Ke[r][4 * j + 1] = w1[i];
                    this.Ke[r][4 * j + 2] = w2[i];
                    this.Ke[r][4 * j + 3] = w3[i];
                    this.Kd[this.numRounds - r][4 * j] = w0[i];
                    this.Kd[this.numRounds - r][4 * j + 1] = w1[i];
                    this.Kd[this.numRounds - r][4 * j + 2] = w2[i];
                    this.Kd[this.numRounds - r][4 * j + 3] = w3[i];
                    ++i;
                }
            }

        }
    }

    public static void self_test(String hkey, String hplain, String hcipher, int lev) {
    }

    public static String static_byteArrayToString(byte[] data) {
        String res = "";
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < data.length; ++i) {
            int n = data[i];
            if(n < 0) {
                n += 256;
            }

            sb.append((char)n);
        }

        res = sb.toString();
        return res;
    }

    public static byte[] static_stringToByteArray(String s) {
        byte[] temp = new byte[s.length()];

        for(int i = 0; i < s.length(); ++i) {
            temp[i] = (byte)s.charAt(i);
        }

        return temp;
    }

    public static String static_intArrayToString(int[] t) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < t.length; ++i) {
            sb.append((char)t[i]);
        }

        return sb.toString();
    }

    public String _cryptAll(String data, int mode) {
        AES aes = this;
        int nParts;
        if(data.length() / 16 > data.length() / 16) {
            nParts = data.length() - data.length() / 16 * 16;

            for(int res = 0; res < nParts; ++res) {
                data = data + " ";
            }
        }

        nParts = data.length() / 16;
        byte[] var10 = new byte[data.length()];
        String partStr = "";
        byte[] partByte = new byte[16];

        for(int p = 0; p < nParts; ++p) {
            partStr = data.substring(p * 16, p * 16 + 16);
            partByte = static_stringToByteArray(partStr);
            if(mode == 1) {
                partByte = aes.encrypt(partByte);
            }

            if(mode == 2) {
                partByte = aes.decrypt(partByte);
            }

            for(int b = 0; b < 16; ++b) {
                var10[p * 16 + b] = partByte[b];
            }
        }

        return static_byteArrayToString(var10);
    }

    public String Encrypt(String data) {
        while(data.length() % 32 != 0) {
            data = data + " ";
        }

        return this._cryptAll(data, 1);
    }

    public String Decrypt(String data) {
        return this._cryptAll(data, 2).trim();
    }

    public byte[] RootEncrypt(String data) {
        while(data.length() % 32 != 0) {
            data = data + " ";
        }

        AES aes = this;
        int nParts = data.length() / 16;
        byte[] res = new byte[data.length()];
        String partStr = "";
        byte[] partByte = new byte[16];

        for(int p = 0; p < nParts; ++p) {
            partStr = data.substring(p * 16, p * 16 + 16);
            partByte = static_stringToByteArray(partStr);
            partByte = aes.encrypt(partByte);

            for(int b = 0; b < 16; ++b) {
                res[p * 16 + b] = partByte[b];
            }
        }

        return res;
    }

    public String RootDecrypt(byte[] data) {
        AES aes = this;
        int nParts = data.length / 16;
        byte[] res = new byte[data.length];
        byte[] partByte = new byte[16];

        for(int p = 0; p < nParts; ++p) {
            System.arraycopy(data, p * 16, partByte, 0, 16);
            partByte = aes.decrypt(partByte);

            for(int b = 0; b < 16; ++b) {
                res[p * 16 + b] = partByte[b];
            }
        }

        return static_byteArrayToString(res).trim();
    }

    public void setKey(String key) {
        this.setKey(static_stringToByteArray(key));
    }

    public static String encryptStrToB64Str(String plain) {
        AES aes = new AES();
        aes.setKey(mSalt);
        return AdBase64.encode(aes.RootEncrypt(plain));
    }

    public static String decryptB64StrToStr(String encrypted) {
        AES aes = new AES();
        aes.setKey(mSalt);
        return aes.RootDecrypt(AdBase64.decode(encrypted));
    }

    private static final class Util {
        public static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private Util() {
        }

        public static byte[] short2byte(short[] sa) {
            int length = sa.length;
            byte[] ba = new byte[length * 2];
            int i = 0;

            short k;
            for(int j = 0; i < length; ba[j++] = (byte)(k & 255)) {
                k = sa[i++];
                ba[j++] = (byte)(k >>> 8 & 255);
            }

            return ba;
        }

        public static short[] byte2short(byte[] ba) {
            int length = ba.length;
            short[] sa = new short[length / 2];
            int i = 0;

            for(int j = 0; j < length / 2; sa[j++] = (short)((ba[i++] & 255) << 8 | ba[i++] & 255)) {
                ;
            }

            return sa;
        }

        public static byte[] int2byte(int[] ia) {
            int length = ia.length;
            byte[] ba = new byte[length * 4];
            int i = 0;

            int k;
            for(int j = 0; i < length; ba[j++] = (byte)(k & 255)) {
                k = ia[i++];
                ba[j++] = (byte)(k >>> 24 & 255);
                ba[j++] = (byte)(k >>> 16 & 255);
                ba[j++] = (byte)(k >>> 8 & 255);
            }

            return ba;
        }

        public static int[] byte2int(byte[] ba) {
            int length = ba.length;
            int[] ia = new int[length / 4];
            int i = 0;

            for(int j = 0; j < length / 4; ia[j++] = (ba[i++] & 255) << 24 | (ba[i++] & 255) << 16 | (ba[i++] & 255) << 8 | ba[i++] & 255) {
                ;
            }

            return ia;
        }

        public static String toHEX(byte[] ba) {
            int length = ba.length;
            char[] buf = new char[length * 3];
            int i = 0;

            for(int j = 0; i < length; buf[j++] = 32) {
                byte k = ba[i++];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
                buf[j++] = HEX_DIGITS[k & 15];
            }

            return new String(buf);
        }

        public static String toHEX(short[] ia) {
            int length = ia.length;
            char[] buf = new char[length * 5];
            int i = 0;

            for(int j = 0; i < length; buf[j++] = 32) {
                short k = ia[i++];
                buf[j++] = HEX_DIGITS[k >>> 12 & 15];
                buf[j++] = HEX_DIGITS[k >>> 8 & 15];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
                buf[j++] = HEX_DIGITS[k & 15];
            }

            return new String(buf);
        }

        public static String toHEX(int[] ia) {
            int length = ia.length;
            char[] buf = new char[length * 10];
            int i = 0;

            for(int j = 0; i < length; buf[j++] = 32) {
                int k = ia[i++];
                buf[j++] = HEX_DIGITS[k >>> 28 & 15];
                buf[j++] = HEX_DIGITS[k >>> 24 & 15];
                buf[j++] = HEX_DIGITS[k >>> 20 & 15];
                buf[j++] = HEX_DIGITS[k >>> 16 & 15];
                buf[j++] = 32;
                buf[j++] = HEX_DIGITS[k >>> 12 & 15];
                buf[j++] = HEX_DIGITS[k >>> 8 & 15];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
                buf[j++] = HEX_DIGITS[k & 15];
            }

            return new String(buf);
        }

        public static String toHEX1(byte b) {
            char[] buf = new char[2];
            byte j = 0;
            int var3 = j + 1;
            buf[j] = HEX_DIGITS[b >>> 4 & 15];
            buf[var3++] = HEX_DIGITS[b & 15];
            return new String(buf);
        }

        public static String toHEX1(byte[] ba) {
            int length = ba.length;
            char[] buf = new char[length * 2];
            int i = 0;

            byte k;
            for(int j = 0; i < length; buf[j++] = HEX_DIGITS[k & 15]) {
                k = ba[i++];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            }

            return new String(buf);
        }

        public static String toHEX1(short[] ia) {
            int length = ia.length;
            char[] buf = new char[length * 4];
            int i = 0;

            short k;
            for(int j = 0; i < length; buf[j++] = HEX_DIGITS[k & 15]) {
                k = ia[i++];
                buf[j++] = HEX_DIGITS[k >>> 12 & 15];
                buf[j++] = HEX_DIGITS[k >>> 8 & 15];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            }

            return new String(buf);
        }

        public static String toHEX1(int i) {
            char[] buf = new char[8];
            byte j = 0;
            int var3 = j + 1;
            buf[j] = HEX_DIGITS[i >>> 28 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 24 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 20 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 16 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 12 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 8 & 15];
            buf[var3++] = HEX_DIGITS[i >>> 4 & 15];
            buf[var3++] = HEX_DIGITS[i & 15];
            return new String(buf);
        }

        public static String toHEX1(int[] ia) {
            int length = ia.length;
            char[] buf = new char[length * 8];
            int i = 0;

            int k;
            for(int j = 0; i < length; buf[j++] = HEX_DIGITS[k & 15]) {
                k = ia[i++];
                buf[j++] = HEX_DIGITS[k >>> 28 & 15];
                buf[j++] = HEX_DIGITS[k >>> 24 & 15];
                buf[j++] = HEX_DIGITS[k >>> 20 & 15];
                buf[j++] = HEX_DIGITS[k >>> 16 & 15];
                buf[j++] = HEX_DIGITS[k >>> 12 & 15];
                buf[j++] = HEX_DIGITS[k >>> 8 & 15];
                buf[j++] = HEX_DIGITS[k >>> 4 & 15];
            }

            return new String(buf);
        }

        public static byte[] hex2byte(String hex) {
            int len = hex.length();
            byte[] buf = new byte[(len + 1) / 2];
            int i = 0;
            int j = 0;
            if(len % 2 == 1) {
                buf[j++] = (byte)hexDigit(hex.charAt(i++));
            }

            while(i < len) {
                buf[j++] = (byte)(hexDigit(hex.charAt(i++)) << 4 | hexDigit(hex.charAt(i++)));
            }

            return buf;
        }

        public static boolean isHex(String hex) {
            int len = hex.length();
            int i = 0;

            char ch;
            do {
                do {
                    do {
                        if(i >= len) {
                            return true;
                        }

                        ch = hex.charAt(i++);
                    } while(ch >= 48 && ch <= 57);
                } while(ch >= 65 && ch <= 70);
            } while(ch >= 97 && ch <= 102);

            return false;
        }

        public static int hexDigit(char ch) {
            return ch >= 48 && ch <= 57?ch - 48:(ch >= 65 && ch <= 70?ch - 65 + 10:(ch >= 97 && ch <= 102?ch - 97 + 10:0));
        }
    }
}