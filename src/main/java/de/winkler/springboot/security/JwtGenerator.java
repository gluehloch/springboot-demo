package de.winkler.springboot.security;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;

import de.winkler.springboot.jwt.KeyStoreService;

public class JwtGenerator {

    @Autowired
    private KeyStoreService keyStoreService;

    public void xxx() {
        PublicKey publicKey = keyStoreService.publicKey().orElseThrow();
        /* PrivateKey */ Key privateKey = keyStoreService.privateKey().orElseThrow();

        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("Public Key:");
        System.out.println(convertToPublicKey(encodedPublicKey));
        String token = generateJwtToken((PrivateKey) privateKey);
        System.out.println("TOKEN:");
        System.out.println(token);

        printStructure(token, publicKey);
    }

    public String generateJwtToken(PrivateKey privateKey) {
        String token = Jwts.builder().setSubject("adam")
                .setExpiration(new Date(2018, 1, 1))
                .setIssuer("info@wstutorial.com")
                .claim("groups", new String[] { "user", "admin" })
                // RS256 with privateKey
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
        return token;
    }

    public void printStructure(String token, PublicKey publicKey) {
        // Jws parseClaimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);

        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(publicKey).build();
        Jws parseClaimsJws = jwtParser.parseClaimsJws(token);

        System.out.println("Header     : " + parseClaimsJws.getHeader());
        System.out.println("Body       : " + parseClaimsJws.getBody());
        System.out.println("Signature  : " + parseClaimsJws.getSignature());
    }

    // Add BEGIN and END comments
    private String convertToPublicKey(String key){
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(key);
        result.append("\n-----END PUBLIC KEY-----");
        return result.toString();
    }

}
