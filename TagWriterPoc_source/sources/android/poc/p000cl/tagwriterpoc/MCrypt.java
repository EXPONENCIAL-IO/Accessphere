package android.poc.p000cl.tagwriterpoc;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* renamed from: android.poc.cl.tagwriterpoc.MCrypt */
public class MCrypt {
    private String SecretKey;
    private Cipher cipher;

    /* renamed from: iv */
    private String f0iv;
    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;

    public MCrypt() {
        this.f0iv = "fedcba9876543210";
        this.SecretKey = "0123456789abcdef";
        this.ivspec = new IvParameterSpec(this.f0iv.getBytes());
        this.keyspec = new SecretKeySpec(this.SecretKey.getBytes(), "AES");
        try {
            this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
    }

    public MCrypt(String SecretKey2) {
        this.f0iv = "fedcba9876543210";
        this.SecretKey = "0123456789abcdef";
        this.ivspec = new IvParameterSpec(this.f0iv.getBytes());
        this.keyspec = new SecretKeySpec(SecretKey2.getBytes(), "AES");
        try {
            this.cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
    }

    public byte[] encrypt(String text) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(1, this.keyspec, this.ivspec);
            return this.cipher.doFinal(padString(text).getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
    }

    public byte[] decrypt(String code) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }
        try {
            this.cipher.init(2, this.keyspec, this.ivspec);
            return this.cipher.doFinal(hexToBytes(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) {
            return null;
        }
        int len = data.length;
        String str = "";
        for (int i = 0; i < len; i++) {
            if ((data[i] & 255) < 16) {
                str = str + "0" + Integer.toHexString(data[i] & 255);
            } else {
                str = str + Integer.toHexString(data[i] & 255);
            }
        }
        return str;
    }

    public static byte[] hexToBytes(String str) {
        byte[] buffer = null;
        if (str != null && str.length() >= 2) {
            int len = str.length() / 2;
            buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, (i * 2) + 2), 16);
            }
        }
        return buffer;
    }

    private static String padString(String source) {
        int padLength = 16 - (source.length() % 16);
        for (int i = 0; i < padLength; i++) {
            source = source + ' ';
        }
        return source;
    }

    public void setIv(String iv) {
    }

    public void setSecretKey(String key) {
    }
}
