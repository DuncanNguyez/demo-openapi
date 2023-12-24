package DemoOpenapi.jwts;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    @Value("${application.security.jwt.expiration}")
    private long accessExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractPayloadProperty(token, Claims::getSubject);
    }

    public <T> T extractPayloadProperty(String token, Function<Claims, T> claimsResolve) {
        Claims claims = extractClaims(token);
        return claimsResolve.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails, new HashMap<>(), accessExpiration);
    }

    public String generateToken(UserDetails userDetails, HashMap<String, Object> payload) {
        return buildToken(userDetails, payload, accessExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, new HashMap<>(), refreshExpiration);
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractPayloadProperty(token, Claims::getExpiration).before(new Date());
    }

    private String buildToken(UserDetails userDetails, HashMap<String, Object> payload, long expired) {
        return Jwts.builder()
                .signWith(generateKey())
                .claims(payload)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expired))
                .compact();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey generateKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
