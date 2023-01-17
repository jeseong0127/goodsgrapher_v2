package com.vitasoft.goodsgrapher.core.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class Encoder {
    public String encrypt(String algorithm, String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());

            return bytesToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new SuchAlgorithmNotExists(algorithm);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}

class SuchAlgorithmNotExists extends RuntimeException {
    public SuchAlgorithmNotExists(String algorithm) {
        super(algorithm + " 알고리즘을 사용할 수 없습니다.\n" +
                "[MD2, MD5, SHA-1, SHA-256, SHA-384, SHA-512] 알고리즘 중 하나를 사용하세요.");
    }
}