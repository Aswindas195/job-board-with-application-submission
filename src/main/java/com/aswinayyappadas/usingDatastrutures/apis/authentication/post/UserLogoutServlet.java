package com.aswinayyappadas.usingDatastrutures.apis.authentication.post;

import com.aswinayyappadas.usingDatabase.apis.authentication.post.UserAuthenticationServlet;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@WebServlet("/api/ds/users/logout/*")
public class UserLogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Obtain the secret key string from UserAuthenticationServlet
    private String secretKeyString = UserAuthenticationServlet.getSecretKeyString();
    private final Key key = Keys.hmacShaKeyFor(Base64.getUrlDecoder().decode(secretKeyString));

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            String token = authToken.substring(7); // Remove "Bearer " prefix

            // Optionally, you can validate the token's signature or perform other checks
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            Claims claims = claimsJws.getBody();

            // Expire the token by setting its expiration date to a past time
            Date expirationDate = new Date(0L); // Set to January 1, 1970, 00:00:00 GMT

            // Build a new token with an expired expiration date
            String expiredToken = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(expirationDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            // Prepare the JSON response with the new expired token
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("status", "success");
            jsonResponse.put("message", "Logout successful.");
            jsonResponse.put("token", expiredToken);

            // Send the JSON response
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println(jsonResponse.toString());
        } else {
            // Token not present or has an invalid format
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.println("{\"status\": \"error\", \"message\": \"Invalid or missing token.\"}");
        }
    }
}
