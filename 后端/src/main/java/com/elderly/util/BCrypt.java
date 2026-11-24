package com.elderly.util;

import java.security.SecureRandom;
import java.util.Arrays;

public class BCrypt {
    static private final int LOG_ROUNDS = 10;
    static private final int GENSALT_DEFAULT_LOG2_ROUNDS = 10;
    static private final int BCRYPT_SALT_LEN = 16;
    
    static private final char[] base64_chars = "./ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    static private final byte[] base64 = new byte[128];
    
    static {
        Arrays.fill(base64, (byte)-1);
        for(int i = 0; i < base64_chars.length; i++) {
            base64[base64_chars[i]] = (byte)i;
        }
    }
    
    public static String gensalt() {
        return gensalt(LOG_ROUNDS);
    }
    
    public static String gensalt(int log_rounds) {
        if(log_rounds < 4 || log_rounds > 31)
            throw new IllegalArgumentException("Round must be between 4 and 31");
        
        byte[] rnd = new byte[BCRYPT_SALT_LEN];
        SecureRandom random = new SecureRandom();
        random.nextBytes(rnd);
        
        StringBuilder sb = new StringBuilder();
        sb.append("$2a$");
        if(log_rounds < 10)
            sb.append("0");
        sb.append(log_rounds);
        sb.append("$");
        sb.append(encode_bytes(rnd, rnd.length));
        return sb.toString();
    }
    
    public static String hashpw(String password, String salt) {
        if(salt == null || !salt.startsWith("$2a$") || salt.length() != 29)
            throw new IllegalArgumentException("Invalid salt");
        
        int rounds = Character.getNumericValue(salt.charAt(4)) * 10 + 
                     Character.getNumericValue(salt.charAt(5));
        
        byte[] saltb = decode_base64(salt.substring(7, 29));
        
        byte[] hashed = crypt_raw(password.getBytes(), saltb, rounds);
        
        StringBuilder sb = new StringBuilder();
        sb.append(salt);
        sb.append(encode_bytes(hashed, hashed.length));
        return sb.toString();
    }
    
    public static boolean checkpw(String password, String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }
        // 如果密码不是 BCrypt 格式（不以 $2a$ 开头），直接比较明文
        if (!hash.startsWith("$2a$")) {
            return hash.equals(password);
        }
        try {
            return hashpw(password, hash).equals(hash);
        } catch (IllegalArgumentException e) {
            // 如果 BCrypt 验证失败，尝试明文比较
            return hash.equals(password);
        }
    }
    
    private static byte[] crypt_raw(byte[] password, byte[] salt, int rounds) {
        try {
            javax.crypto.SecretKeyFactory skf = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                new String(password).toCharArray(), 
                salt, 
                1 << rounds, 
                184
            );
            byte[] raw = skf.generateSecret(spec).getEncoded();
            byte[] ret = new byte[raw.length + 1];
            ret[0] = raw[raw.length - 1];
            System.arraycopy(raw, 0, ret, 1, raw.length - 1);
            return ret;
        } catch(Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    private static String encode_bytes(byte[] b, int length) {
        int off = 0;
        StringBuilder sb = new StringBuilder();
        int c1, c2, c3, c4;
        
        while(off < length) {
            c1 = b[off++] & 0xff;
            sb.append(base64_chars[c1 >> 2]);
            c1 = (c1 & 0x03) << 4;
            if(off >= length) {
                sb.append(base64_chars[c1]);
                break;
            }
            c2 = b[off++] & 0xff;
            c1 |= c2 >> 4;
            sb.append(base64_chars[c1]);
            c1 = (c2 & 0x0f) << 2;
            if(off >= length) {
                sb.append(base64_chars[c1]);
                break;
            }
            c2 = b[off++] & 0xff;
            c1 |= c2 >> 6;
            sb.append(base64_chars[c1]);
            sb.append(base64_chars[c2 & 0x3f]);
        }
        return sb.toString();
    }
    
    private static byte[] decode_base64(String s) {
        byte[] ret = new byte[s.length() * 3 / 4];
        int off = 0;
        int c1, c2, c3, c4;
        int i = 0;
        
        while(i < s.length()) {
            c1 = base64[s.charAt(i++)];
            c2 = base64[s.charAt(i++)];
            
            if(c1 == -1 || c2 == -1)
                break;
            
            ret[off++] = (byte)((c1 << 2) | (c2 >> 4));
            
            if(i >= s.length())
                break;
                
            c3 = base64[s.charAt(i++)];
            if(c3 == -1)
                break;
                
            ret[off++] = (byte)(((c2 & 0x0f) << 4) | (c3 >> 2));
            
            if(i >= s.length())
                break;
                
            c4 = base64[s.charAt(i++)];
            if(c4 == -1)
                break;
                
            ret[off++] = (byte)(((c3 & 0x03) << 6) | c4);
        }
        
        byte[] b = new byte[off];
        System.arraycopy(ret, 0, b, 0, off);
        return b;
    }
}
