package hr.algebra.semregprojectbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders; // Ovo je iz novijih verzija
import io.jsonwebtoken.security.Keys; // Ovo je iz novijih verzija
import org.springframework.beans.factory.annotation.Value; // Dodano za eksternalizaciju tajnog ključa
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // PROMJENA JE OVDJE: Koristi parserBuilder() umjesto parser()
        return Jwts
                .parserBuilder() // <-- Promijenjeno iz .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        // Vaš expiration time (1000*6000*1) je vrlo dug (100 sati). Ako je namjerno, ok.
        // Ako ne, razmislite o kraćem. Npr. 1000L * 60 * 60 * 24 za 24 sata.
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 100)) // Primjer za 100 sati
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        // PAŽNJA: Ovisno o formatu vašeg SECRET ključa, Decoders.BASE64.decode() možda nije ispravno.
        // Ako je ključ heksadecimalni string, trebat će vam Apache Commons Codec Hex.decodeHex().
        // Za sada pretpostavljam da je Base64 ili da to radi (ako su ranije verzije jjwt to tolerirale).
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}