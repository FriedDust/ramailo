package com.ramailo.util;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ramailo.annotation.Logged;

@Logged
public class HashUtility {

	private static final Logger LOGGER = LoggerFactory.getLogger(HashUtility.class);

	public String hash(String string) {

		return BCrypt.hashpw(string, BCrypt.gensalt(12));
	}

	public Boolean match(String str1, String str2) {
		try {
			return BCrypt.checkpw(str1, str2);
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Exception {}", e);
			return false;
		}
	}
}
