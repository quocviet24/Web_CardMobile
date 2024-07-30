package com.nishikatakagi.ProductDigital.service_impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.springframework.stereotype.Service;

import com.nishikatakagi.ProductDigital.service.SecurityService;

@Service
public class SecurityServiceImpl implements SecurityService{
    public String encode(String password) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			byte[] bytes = md.digest();
			StringBuilder sb = new StringBuilder();
			for (byte b : bytes) {
				sb.append(String.format("%02x", b));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}


}