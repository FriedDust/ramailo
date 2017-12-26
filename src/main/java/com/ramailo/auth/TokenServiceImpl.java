package com.ramailo.auth;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.auth0.jwt.internal.org.apache.commons.codec.binary.Base64;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramailo.annotation.Logged;
import com.ramailo.auth.dto.RefreshTokenDTO;
import com.ramailo.auth.dto.TokenDTO;
import com.ramailo.service.IdentityService;

@Logged
public class TokenServiceImpl implements TokenService {

	private static final String ISSUER = "ramailo";
	private static final String SECRET = "ramailo";

	private static final String ISS = "iss";
	private static final String SUB = "sub";
	private static final String IAT = "iat";
	private static final String EXP = "exp";

	private static final String TOKEN_TYPE = "token_type";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String ACCESS_TOKEN = "access_token";

	private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

	@Inject
	private IdentityService identityService;

	public String generateToken(String userJson, Long expiryTimeInMinutes, String tokenType) {
		final JWTSigner jwtSigner = new JWTSigner(SECRET);

		LocalDateTime now = LocalDateTime.now();

		Base64 encoder = new Base64(true);
		String encodedUserJson = encoder.encodeToString(userJson.getBytes());

		Map<String, Object> claims = new HashMap<>();
		claims.put(ISS, ISSUER);
		claims.put(SUB, encodedUserJson);
		claims.put(IAT, now.atZone(ZoneId.systemDefault()).toEpochSecond());
		claims.put(EXP, now.plusMinutes(expiryTimeInMinutes).atZone(ZoneId.systemDefault()).toEpochSecond());

		if (tokenType.equals(ACCESS_TOKEN)) {
			claims.put(TOKEN_TYPE, ACCESS_TOKEN);
		} else {
			claims.put(TOKEN_TYPE, REFRESH_TOKEN);
		}

		return jwtSigner.sign(claims);
	}

	private String generateRefreshToken(String userJson) {
		return generateToken(userJson, 1440l, REFRESH_TOKEN);
	}

	private String generateAccessToken(String userJson) {
		return generateToken(userJson, 10l, ACCESS_TOKEN);
	}

	public Map<String, Object> validateToken(String token) {
		final JWTVerifier jwtVerifier = new JWTVerifier(SECRET);

		try {
			return jwtVerifier.verify(token);

		} catch (JWTVerifyException | NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException
				| IllegalStateException e) {
			LOGGER.warn("invalid or expired jwt token");
			throw new InvalidTokenException("Invalid or expired token");
		}
	}

	public Identity getUserInfo(String token) {
		Map<String, Object> claims = validateToken(token);
		String sub = (String) claims.get(SUB);
		String userId = new String(Base64.decodeBase64(sub.getBytes()));

		ObjectMapper mapper = new ObjectMapper();
		try {
			Integer id = mapper.readValue(userId, Integer.class);
			return identityService.fetchById(id);
		} catch (IOException e) {
			LOGGER.error("exception ,{}", e);
			throw new InternalServerErrorException();
		}
	}

	public String getTokenType(String token) {
		Map<String, Object> claims = validateToken(token);
		String tokenType = (String) claims.get(TOKEN_TYPE);
		return tokenType.equals(ACCESS_TOKEN) ? ACCESS_TOKEN : REFRESH_TOKEN;
	}

	public TokenDTO getNewAccessToken(RefreshTokenDTO refreshTokenDTO) {
		String tokenType = getTokenType(refreshTokenDTO.getRefreshToken());

		if (!tokenType.equals(REFRESH_TOKEN)) {
			LOGGER.warn("Invalid token type");
			throw new InvalidTokenException("Invalid or expired token");
		}

		Identity identity = getUserInfo(refreshTokenDTO.getRefreshToken());
		String accessToken = generateAccessToken(identity.getId().toString());

		TokenDTO tokenDTO = new TokenDTO();
		tokenDTO.setRefreshToken(refreshTokenDTO.getRefreshToken());
		tokenDTO.setAccessToken(accessToken);
		return tokenDTO;
	}

	public TokenDTO issueToken(String userId) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String userInfo = mapper.writeValueAsString(userId);
			String accessToken = generateAccessToken(userInfo);
			String refreshToken = generateRefreshToken(userInfo);

			TokenDTO tokenDTO = new TokenDTO();
			tokenDTO.setAccessToken(accessToken);
			tokenDTO.setRefreshToken(refreshToken);
			return tokenDTO;

		} catch (JsonProcessingException e) {
			LOGGER.error("exception, {}", e);
			throw new InternalServerErrorException();
		}
	}
}
