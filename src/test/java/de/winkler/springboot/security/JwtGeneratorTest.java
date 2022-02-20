package de.winkler.springboot.security;

import java.security.PrivateKey;
import java.security.PublicKey;

import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.winkler.springboot.jwt.KeyStoreService;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class JwtGeneratorTest {

    @Autowired
    private KeyStoreService keyStoreService;

    @Autowired
    private JwtGenerator jwtGenerator;

    @DisplayName("Generate a JWT with the configured key store.")
    @Tag("jwt")
    @Test
    void generateJwt() {
        String encodedPublicKey = jwtGenerator.encodedPublicKey();

        System.out.println("Public Key:");
        System.out.println(convertToPublicKey(encodedPublicKey));

        String token = jwtGenerator.generateJwtToken();

        System.out.println("TOKEN:");
        System.out.println(token);

        PublicKey publicKey = keyStoreService.publicKey().orElseThrow();
        printStructure(token, publicKey);
    }

    public void printStructure(String token, PublicKey publicKey) {
        // Jws parseClaimsJws = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);

        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(publicKey).build();
        Jws parseClaimsJws = jwtParser.parseClaimsJws(token);

        System.out.println("Header     : " + parseClaimsJws.getHeader());
        System.out.println("Body       : " + parseClaimsJws.getBody());
        System.out.println("Signature  : " + parseClaimsJws.getSignature());
    }

    private String convertToPublicKey(String key){
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(key);
        result.append("\n-----END PUBLIC KEY-----");
        return result.toString();
    }
}
