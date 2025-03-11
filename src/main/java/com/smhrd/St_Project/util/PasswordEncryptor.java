package com.smhrd.St_Project.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {

	  /**
     * 🔹 SHA-256 암호화 메서드
     * - 입력된 비밀번호를 SHA-256으로 해싱하여 반환
     */
    public static String encryptSHA256(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 null 또는 비어 있을 수 없습니다.");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // ✅ UTF-8 인코딩을 강제 적용하여 일관된 해시값 생성
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 암호화 오류 발생", e);
        }
    }

}
