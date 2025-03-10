package com.smhrd.St_Project.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptor {

    // SHA-256 암호화 메서드
	public static String encryptSHA256(String password) {
	    if (password == null || password.isEmpty()) {
	        System.out.println("암호화 오류: 비밀번호가 null 또는 비어있음");
	        return null; // 또는 예외 발생
	    }

	    try {
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] hash = md.digest(password.getBytes());
	        StringBuilder hexString = new StringBuilder();

	        for (byte b : hash) {
	            hexString.append(String.format("%02x", b));
	        }
	        return hexString.toString();
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("암호화 알고리즘 오류", e);
	    }
	}

}
