package com.zooting.api.global.jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
    private final String issuer = "Zooting";
    private final SecretKey secretKey;
    private final long validityInMilliseconds;

    public JwtService(
            @Value("${jwt.secretKey}") String secretKey,
            @Value("${jwt.expiration-time}") long validityInMilliseconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(String userEmail){
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .claim("userEmail", userEmail)
                .signWith(secretKey, Jwts.SIG.HS512)
                .issuer(issuer)
                .expiration(expirationDate)
                .subject(userEmail)
                .compact();
    }



}
