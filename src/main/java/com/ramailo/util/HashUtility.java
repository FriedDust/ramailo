package com.ramailo.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramailo.annotation.Logged;

@Logged
public class HashUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashUtility.class);

	public String hashPassword(String password) {

		return BCrypt.hashpw(password, BCrypt.gensalt(12));
	}

	public Boolean checkPassword(String password, String hashedPassword) {
		try {
			return BCrypt.checkpw(password, hashedPassword);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}
}
