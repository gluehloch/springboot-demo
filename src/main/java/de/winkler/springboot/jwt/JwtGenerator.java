package de.winkler.springboot.jwt;

import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import de.winkler.springboot.datetime.TimeService;

@Component
public class JwtGenerator {

    private final KeyStoreService keyStoreService;

    public JwtGenerator(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    /**
     * Liest einen Base64 'encoded' 'public key' aus dem KeyStore.
     *
     * @return Ein encoded public key
     */
    public String encodedPublicKey() {
        PublicKey publicKey = keyStoreService.publicKey().orElseThrow();
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String generateJwtToken() {
        Key privateKey = keyStoreService.privateKey().orElseThrow();

        String token = Jwts.builder().setSubject("adam")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .setIssuer("info@wstutorial.com")
                .claim("groups", new String[] { "user", "admin" })
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return token;
    }

    public String generateJwtToken(JwtProperty jwtProperty) {
        Key privateKey = keyStoreService.privateKey().orElseThrow();

        String token = Jwts.builder()
                .setSubject(jwtProperty.subject())
                .setExpiration(TimeService.convertToDateViaInstant(jwtProperty.expiration()))
                .setIssuer(jwtProperty.issuer())
                .claim("groups", jwtProperty.claims())
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return token;
    }

}
