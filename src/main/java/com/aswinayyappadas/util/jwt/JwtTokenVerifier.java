package com.aswinayyappadas.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class JwtTokenVerifier {

    public boolean verifyToken(String token, String email, String jwtSecretKey) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Decode the Base64 URL encoded secret key
            byte[] decodedSecretKey = Base64.getUrlDecoder().decode(jwtSecretKey);

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(decodedSecretKey)).build()
                    .parseClaimsJws(token).getBody();

            // Additional checks if needed
            // e.g., check claims.get("iss"), claims.get("aud"), claims.get("nbf"), claims.getExpiration(), etc.

            // Verify email from obtained JWT and the email from the database
            return email.equals(claims.getSubject());
        } catch (JwtException e) {
            // Token verification failed
            e.printStackTrace(); // Log the exception for debugging
            return false;
        }
    }
}