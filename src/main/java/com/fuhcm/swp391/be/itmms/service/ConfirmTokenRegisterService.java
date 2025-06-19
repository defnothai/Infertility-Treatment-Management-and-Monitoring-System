package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

@Service
public class ConfirmTokenRegisterService {

    private final AuthenticationService authenticationService;
    @Value("${secret-key}")
    private String SECRET_KEY;

    public ConfirmTokenRegisterService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    private SecretKey getSigninKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Account account, LocalDateTime createdAt) {
        String token = Jwts.builder()
                .subject(account.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+24*60*60*1000))
                .claim("createdAt", createdAt.toString())
                .signWith(this.getSigninKey())
                .compact();
        return token;
    }

    public Claims extractAllClaims(String token) {
        return  Jwts.parser().
                verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Account extractAccount (String token){
        String email = extractClaim(token,Claims::getSubject);
        return authenticationService.findByEmail(email);
    }

    public LocalDateTime extractCreatedAt(String token) {
        String createdAtStr = extractClaim(token, claims -> claims.get("createdAt", String.class));
        return LocalDateTime.parse(createdAtStr);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    public boolean isValidVersionToken(String token) {
        return extractAccount(token).getCreatedAt().withNano(0)
                .equals(extractCreatedAt(token).withNano(0));
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver){
        Claims claims = extractAllClaims(token);
        return  resolver.apply(claims);

    }

}
