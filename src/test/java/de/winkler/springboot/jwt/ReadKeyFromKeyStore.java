package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ReadKeyFromKeyStore {

    @DisplayName("Read a key from a Java KeyStore file.")
    @Tag("keystore")
    @Test
    public void readKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            FileNotFoundException, IOException, UnrecoverableKeyException {

        String jksPassword = "awtest666";

        // Read the private key from the KeyStore file.
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(ReadKeyFromKeyStore.class.getResourceAsStream("awtest.jks"), jksPassword.toCharArray());
        Key key = ks.getKey("awtest", jksPassword.toCharArray());

        assertThat(key).isNotNull();

        String compactJws = Jwts.builder()
                .setSubject("Joe")
                .setAudience("testAudienceId")
                .setExpiration(new Date(2020, 5, 24))
                .setIssuedAt(new Date(2020, 2, 4))
                .setId("testuserid")
                .signWith(SignatureAlgorithm.RS512, key)
                .compact();

        System.out.println(compactJws);

        PublicKey publicKey = loadPublicKey();
        Jws<Claims> x = Jwts.parser().setSigningKey(publicKey).parseClaimsJws(compactJws);
        
        String id = x.getBody().getId();
        System.out.println("id: " + id);
        System.out.println("audience: " + x.getBody().getAudience());
        System.out.println("audience: " + x.getBody().getSubject());        
    }

    public static PublicKey loadPublicKey() throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(ReadKeyFromKeyStore.class.getResourceAsStream("awtest.cer"));
        PublicKey retVal = cert.getPublicKey();
        return retVal;
    }

}
