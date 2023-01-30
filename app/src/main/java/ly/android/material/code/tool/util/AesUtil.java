package ly.android.material.code.tool.util;


import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import ly.android.material.code.natives.MaterialCodeToolNative;

public class AesUtil {

    private static final String ALGORITHM = "AES";
    private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";//AES/CBC/PKCS7Padding
    private static final String AES_ECB_PADDING = "AES/ECB/PKCS5Padding";//AES/ECB/PKCS7Padding

    private static final String desKey = MaterialCodeToolNative.INSTANCE.getKey();

    private static final String IvKey = MaterialCodeToolNative.INSTANCE.getIvKey();

    private static SecretKey getSecretKey(byte[] key) {
        try {
            //获取指定的密钥生成器
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            //加密强随机数
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.setSeed(key);
            //这里可以是128、192、256、越大越安全
            keyGen.init(256, secureRandom);
            return keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("AES获取密钥出现错误,算法异常");
        }
    }

    public static byte[] encodeByCBC(byte[] key, byte[] keyIv, byte[] data)
            throws Exception {
        //获取SecretKey对象,也可以使用getSecretKey()方法
        Key secretKey = new SecretKeySpec(key, ALGORITHM);
        //获取指定转换的密码对象Cipher（参数：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        //创建向量参数规范也就是初始化向量
        IvParameterSpec ips = new IvParameterSpec(keyIv);
        //用密钥和一组算法参数规范初始化此Cipher对象（加密模式）
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ips);
        //执行加密操作
        return cipher.doFinal(data);
    }

    public static byte[] decodeByCBC(byte[] key, byte[] keyIv, byte[] data)
            throws Exception {
        //获取SecretKey对象,也可以使用getSecretKey()方法
        Key secretKey = new SecretKeySpec(key, ALGORITHM);
        //获取指定转换的密码对象Cipher（参数：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
        //创建向量参数规范也就是初始化向量
        IvParameterSpec ips = new IvParameterSpec(keyIv);
        //用密钥和一组算法参数规范初始化此Cipher对象（加密模式）
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ips);
        //执行加密操作
        return cipher.doFinal(data);
    }

    public static byte[] encodeByECB(byte[] key, byte[] data) throws Exception {
        //获取SecretKey对象,也可以使用getSecretKey()方法
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        //获取指定转换的密码对象Cipher（参数：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        //用密钥和一组算法参数规范初始化此Cipher对象（加密模式）
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        //执行加密操作
        return cipher.doFinal(data);
    }

    public static byte[] decodeByECB(byte[] key, byte[] data) throws Exception {
        //获取SecretKey对象,也可以使用getSecretKey()方法
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        //获取指定转换的密码对象Cipher（参数：算法/工作模式/填充模式）
        Cipher cipher = Cipher.getInstance(AES_ECB_PADDING);
        //用密钥和一组算法参数规范初始化此Cipher对象（加密模式）
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        //执行加密操作
        return cipher.doFinal(data);
    }

    public synchronized static String encode(String content){
        try {
            byte[] key = desKey.getBytes(StandardCharsets.UTF_8);
            byte[] data = content.getBytes(StandardCharsets.UTF_8);
            byte[] encode = AesUtil.encodeByCBC(key, IvKey.getBytes(StandardCharsets.UTF_8), data);
            return encode64(encode);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public synchronized static String decode(String desContent){
        try {
            byte[] key1 = desKey.getBytes(StandardCharsets.UTF_8);
            byte[] data1 = decode64(desContent);
            byte[] decodeStr = AesUtil.decodeByCBC(key1, IvKey.getBytes(StandardCharsets.UTF_8), data1);
           return new String(decodeStr, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static char[] base64EncodeChars = new char[]
            { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
                    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
                    '6', '7', '8', '9', '+', '/' };
    private static byte[] base64DecodeChars = new byte[]
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53,
                    54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11,
                    12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29,
                    30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
                    -1, -1, -1 };

    public static String encode64(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;
        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len){
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4) | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2) | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    public static byte[] decode64(String str) {
        try{
            return decodePrivate(str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[]
                {};
    }

    private static byte[] decodePrivate(String str) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        byte[] data = null;
        data = str.getBytes("US-ASCII");
        int len = data.length;
        int i = 0;
        int b1, b2, b3, b4;
        while (i < len){
            do {
                b1 = base64DecodeChars[data[i++]];
            } while (i < len && b1 == -1);
            if (b1 == -1)
                break;
            do {
                b2 = base64DecodeChars[data[i++]];
            } while (i < len && b2 == -1);
            if (b2 == -1)
                break;
            sb.append((char) ((b1 << 2) | ((b2 & 0x30) >>> 4)));
            do{
                b3 = data[i++];
                if (b3 == 61)
                    return sb.toString().getBytes("iso8859-1");
                b3 = base64DecodeChars[b3];
            } while (i < len && b3 == -1);
            if (b3 == -1)
                break;
            sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
            do {
                b4 = data[i++];
                if (b4 == 61)
                    return sb.toString().getBytes("iso8859-1");
                b4 = base64DecodeChars[b4];
            } while (i < len && b4 == -1);
            if (b4 == -1)
                break;
            sb.append((char) (((b3 & 0x03) << 6) | b4));
        }
        return sb.toString().getBytes("iso8859-1");
    }
}