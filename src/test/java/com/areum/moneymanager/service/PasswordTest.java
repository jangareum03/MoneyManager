package com.areum.moneymanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

public class PasswordTest {

	PasswordEncoder encoder = new BCryptPasswordEncoder();

	@Test
	public void passwordEncode() {
		//given
		String rawPwd = "$10$p2GSbjXODlpHVyjJAvOTGOYBFBRS24Zt1HS25NStJMYpTjqS2goCK";

		//when
		String encodePassword = encoder.encode(rawPwd);

		//then
		System.out.println("암호화된 비밀번호 : " + encodePassword);
		boolean isMatch = encoder.matches( rawPwd, encodePassword );
		Assertions.assertTrue(isMatch);
	}
}