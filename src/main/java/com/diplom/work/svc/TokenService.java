package com.diplom.work.svc;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class TokenService {
    public String generateToken() {
        return randomString(64);
    }

    private static String randomString(int len) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
