package com.project.quanlycanghangkhong.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.main.web-application-type", havingValue = "servlet", matchIfMissing = true)
public class JwtTokenProvider {

	@Value("${app.jwt-secret:}")
	private String jwtSecret;

	@Value("${app.jwt-expiration-milliseconds:86400000}")
	private int jwtExpirationInMs;

	private Key getSigningKey() {
		if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
			throw new IllegalStateException(
					"JWT secret is not configured. Please set app.jwt-secret in your environment.");
		}
		byte[] keyBytes = jwtSecret.getBytes();
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(getSigningKey(), SignatureAlgorithm.HS512)
				.compact();
	}

	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (SecurityException ex) {
			log.error("Invalid JWT signature: {}", ex.getMessage());
			throw new IllegalArgumentException("Chữ ký JWT không hợp lệ.");
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT token: {}", ex.getMessage());
			throw new IllegalArgumentException("Token JWT không hợp lệ.");
		} catch (ExpiredJwtException ex) {
			log.error("Expired JWT token: {}", ex.getMessage());
			throw new IllegalArgumentException("Token JWT đã hết hạn.");
		} catch (UnsupportedJwtException ex) {
			log.error("Unsupported JWT token: {}", ex.getMessage());
			throw new IllegalArgumentException("Token JWT không được hỗ trợ.");
		} catch (IllegalArgumentException ex) {
			log.error("JWT claims string is empty: {}", ex.getMessage());
			throw new IllegalArgumentException("Chuỗi claims của JWT trống.");
		}
	}
}
