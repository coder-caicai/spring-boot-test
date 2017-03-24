package com.hbc.api.util;

import java.io.UnsupportedEncodingException;

public class HEX {
    private static final char[] DIGITS = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};

    public static byte[] decodeHex(char[] paramArrayOfChar) {
        int j = 0;
        int k = paramArrayOfChar.length;
        if ((k & 0x1) != 0)
            throw new RuntimeException("Odd number of characters.");
        byte[] arrayOfByte = new byte[k >> 1];
        int i = 0;
        while (true) {
            if (j >= k)
                return arrayOfByte;
            int m = toDigit(paramArrayOfChar[j], j);
            j += 1;
            int n = toDigit(paramArrayOfChar[j], j);
            j += 1;
            arrayOfByte[i] = ((byte) ((m << 4 | n) & 0xFF));
            i += 1;
        }
    }

    public static byte[] decodeHexString(String paramString) {
        return decodeHex(paramString.toCharArray());
    }

    public static char[] encodeHex(byte[] paramArrayOfByte) {
        int j = 0;
        int k = paramArrayOfByte.length;
        char[] arrayOfChar = new char[k << 1];
        int i = 0;
        while (true) {
            if (i >= k)
                return arrayOfChar;
            int m = j + 1;
            arrayOfChar[j] = DIGITS[((paramArrayOfByte[i] & 0xF0) >>> 4)];
            j = m + 1;
            arrayOfChar[m] = DIGITS[(paramArrayOfByte[i] & 0xF)];
            i += 1;
        }
    }

    public static String encodeHexString(byte[] paramArrayOfByte) {
        return new String(encodeHex(paramArrayOfByte));
    }

    protected static int toDigit(char paramChar, int paramInt) {
        int i = Character.digit(paramChar, 16);
        if (i == -1)
            throw new RuntimeException("Illegal hexadecimal charcter " + paramChar + " at index " + paramInt);
        return i;
    }

    public static String toHex(byte[] paramArrayOfByte) {
        StringBuffer localStringBuffer = new StringBuffer();
        int i = 0;
        while (true) {
            if (i >= paramArrayOfByte.length)
                return localStringBuffer.toString();
            localStringBuffer.append(String.format("%02x", new Object[]{Byte.valueOf(paramArrayOfByte[i])}));
            i += 1;
        }
    }

    public byte[] decode(Object paramObject) {
        try {
            return decodeHex(paramObject.toString().toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decode(byte[] paramArrayOfByte) {
        return decodeHex(new String(paramArrayOfByte).toCharArray());
    }

    public byte[] encode(byte[] paramArrayOfByte) {
        return new String(encodeHex(paramArrayOfByte)).getBytes();
    }

    public char[] encode(Object paramObject) {
        try {
            return encodeHex(paramObject.toString().getBytes());
        } catch (Exception e)

        {
            e.printStackTrace();
        }
        return null;
    }

    public static String hexDecode(String src) {
        char[] srcArr = src.toCharArray();
        byte[] retArr = new byte[srcArr.length];
        for (int i = 0; i < srcArr.length; i++) {
            retArr[i] = (byte) srcArr[i];
        }
        try {
            return new String(retArr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            //do nothing
        }
        return null;
    }

}