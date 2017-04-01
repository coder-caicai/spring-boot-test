package com.hbc.api.util;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESedeCoder {
    /**
     * 密钥算法
     */
    private static final String KEY_ALGORITHM = "DESede";

    private static final String DEFAULT_CIPHER_ALGORITHM = "DESede/CBC/PKCS5Padding";
//    private static final String DEFAULT_CIPHER_ALGORITHM = "DESede/ECB/ISO10126Padding";  

    /**
     * 初始化密钥
     *
     * @return byte[] 密钥
     * @throws Exception
     */
    public static byte[] initSecretKey() throws Exception {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象  
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        //初始化此密钥生成器，使其具有确定的密钥大小  
        kg.init(168);
        //生成一个密钥  
        SecretKey secretKey = kg.generateKey();
//        return secretKey.getEncoded(); 
//        return "548e3b79fe75110271069efe".getBytes();
//        String keys = "548e3b79fe75110271069efe";
        String keys = "1234567`90koiuyhgtfrdewsaqaqsqde";
        byte[] result = new byte[24];
        System.arraycopy(keys.getBytes(), 0, result, 0, result.length);
        return result;
//      return "abcdefg`i`koiuyhgtfrdewsaqaqsqde".getBytes();
    }

    /**
     * 转换密钥
     *
     * @param key 二进制密钥
     * @return Key  密钥
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        //实例化DES密钥规则  
        DESedeKeySpec dks = new DESedeKeySpec(key);
        //实例化密钥工厂  
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        //生成密钥  
        SecretKey secretKey = skf.generateSecret(dks);
        return secretKey;
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  二进制密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }


    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        //还原密钥  
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }

    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为加密模式  
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0}));
        //执行操作  
        return cipher.doFinal(data);
    }


    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  二进制密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             二进制密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
        //还原密钥  
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化  
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式  
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[]{0, 0, 0, 0, 0, 0, 0, 0}));
        //执行操作  
        return cipher.doFinal(data);
    }

    private static String showByteArray(byte[] data) {
        if (null == data) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for (byte b : data) {
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    /**
     * 加密
     *
     * @param data
     * @return
     * @throws Exception
     */
    public static String doEncryptData(String data) {
        byte[] key = null;
        byte[] encryptData = null;
        try {
            key = initSecretKey();
            encryptData = encrypt(data.getBytes(), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return HEX.encodeHexString(encryptData);
    }

    public static String doEncryptDataBase64(String data) {
        byte[] key = null;
        byte[] encryptData = null;
        try {
            key = initSecretKey();
            encryptData = encrypt(data.getBytes(), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64.encodeBase64String(encryptData);
    }

    public static String doDecryptData(String data) {
        byte[] key = new byte[0];
        byte[] decryptData = null;
        try {
            key = initSecretKey();
            decryptData = decrypt(HEX.decodeHexString(data), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptData);
    }

    public static String doDecryptDataBase64(String data) {
        byte[] key = new byte[0];
        byte[] decryptData = null;
        try {
            key = initSecretKey();
            decryptData = decrypt(Base64.decodeBase64(data), key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptData);
    }


}
