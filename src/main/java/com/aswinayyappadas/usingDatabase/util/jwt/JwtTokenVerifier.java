package com.aswinayyappadas.usingDatabase.util.jwt;

import com.aswinayyappadas.usingDatabase.apis.authentication.post.UserAuthenticationServlet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;

public class JwtTokenVerifier {

    public boolean verifyToken(String token, String email) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Decode the Base64 URL encoded secret key
            byte[] decodedSecretKey = Base64.getUrlDecoder().decode(UserAuthenticationServlet.getSecretKeyString());

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

    public String extractUserEmail(String authToken) {
        try {
            if (authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }

            // Use getSecretKey method to get the secret key
            byte[] decodedSecretKey = Base64.getUrlDecoder().decode(UserAuthenticationServlet.getSecretKeyString());

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(decodedSecretKey)).build()
                    .parseClaimsJws(authToken).getBody();

            return claims.getSubject(); // Returns the user email
        } catch (JwtException e) {
            // Token verification failed
            e.printStackTrace(); // Log the exception for debugging
            return null;
        }
    }

    public int extractUserId(String authToken) {
        try {
            if (authToken.startsWith("Bearer ")) {
                authToken = authToken.substring(7);
            }
            String jwtSecretKey = UserAuthenticationServlet.getSecretKeyString();
            // Use getSecretKey method to get the secret key
            byte[] decodedSecretKey = Base64.getUrlDecoder().decode(jwtSecretKey);

            Claims claims = Jwts.parserBuilder().setSigningKey(Keys.hmacShaKeyFor(decodedSecretKey)).build()
                    .parseClaimsJws(authToken).getBody();

            // Extract user ID from claims
            return claims.get("userId", Integer.class); // Assuming user ID is stored as "jti" (JWT ID)
        } catch (JwtException e) {
            // Token verification failed
            e.printStackTrace(); // Log the exception for debugging
            return -1; // Return a default value or handle as appropriate
        }
    }
}
