package com.hbc.api.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by cheng on 16/11/8.
 */
public class AesUtil {

    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
//    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";


    private static final  String KEY = "e28a4f2d9eb6aa3a2f3484cb368a73e9";


    public static byte[] initSecretKey() {
        //返回生成指定算法的秘密密钥的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[0];
        }
        //初始化此密钥生成器，使其具有确定的密钥大小
        //AES 要求密钥长度为 128
        kg.init(128);
        //生成一个密钥
        SecretKey  secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }


    private static Key toKey(byte[] key){
        //生成密钥
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }


    public static byte[] encrypt(byte[] data,Key key) throws Exception{
        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }


//    public static byte[] encrypt(byte[] data,byte[] key) throws Exception{
//        return encrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
//    }



    public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
        //还原密钥
        Key k = toKey(key);
        return encrypt(data, k, cipherAlgorithm);
    }


    public static byte[] encrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }




//    public static byte[] decrypt(byte[] data,byte[] key) throws Exception{
//        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
//    }


    public static byte[] decrypt(byte[] data,Key key) throws Exception{
        return decrypt(data, key,DEFAULT_CIPHER_ALGORITHM);
    }


    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm) throws Exception{
        //还原密钥
        Key k = toKey(key);
        return decrypt(data, k, cipherAlgorithm);
    }

    public static byte[] decrypt(byte[] data,Key key,String cipherAlgorithm) throws Exception{
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }

    private static String  showByteArray(byte[] data){
        if(null == data){
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        for(byte b:data){
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append("}");
        return sb.toString();
    }


    public static String  doEncrypt(String data)  {
        Key k = toKey(HEX.decodeHexString(KEY));
        byte[] encryptData = new byte[0];
        try {
            encryptData = encrypt(data.getBytes(), k);
            System.out.println("#########:"+encryptData);
//            return HEX.encodeHexString(encryptData);
            return Base64.getEncoder().encodeToString(encryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doDecrypt(String data) {
        Key k = toKey(HEX.decodeHexString(KEY));
        byte[] decryptData = new byte[0];
        try {
            decryptData = decrypt(Base64.getDecoder().decode(data),k);
//            decryptData = decrypt(HEX.decodeHexString(data), k);
            return new String(decryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) throws Exception {
//        //生成密钥
////        byte[] key = initSecretKey();
////        String keyStr = HEX.encodeHexString(key);
////        System.out.println(keyStr);
//
//        String key = "e28a4f2d9eb6aa3a2f3484cb368a73e9";
////        Key k = toKey(HEX.decodeHexString(key));
//
////        String data ="才成就的垃圾";
////        String result = doEncrypt(data);
////        System.out.println("加密后数据: :"+ result);
////        System.out.println();
//        String dresult = doDecrypt("oeitu2+JLwuB8eBPYu40WlWCmUHL0LWn9mxxajb4YNw=");
//        System.out.println("解密后数据: string:"+dresult);
//
//    }
}
