package de.winkler.springboot.security;

import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.winkler.springboot.jwt.KeyStoreService;

@Component
public class JwtGenerator {

    @Autowired
    private final KeyStoreService keyStoreService;

    @Autowired
    public JwtGenerator(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    public String encodedPublicKey() {
        PublicKey publicKey = keyStoreService.publicKey().orElseThrow();
        /* PrivateKey */ Key privateKey = keyStoreService.privateKey().orElseThrow();

        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String generateJwtToken() {
        Key privateKey = keyStoreService.privateKey().orElseThrow();

        String token = Jwts.builder().setSubject("adam")
                .setExpiration(new Date(2018, 1, 1))
                .setIssuer("info@wstutorial.com")
                .claim("groups", new String[] { "user", "admin" })
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
        return token;
    }

}
