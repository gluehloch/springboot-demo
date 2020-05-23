package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 *
 * KeyStore erstellen:
 * <pre>
 * keytool -genkey -alias awtest -keyalg RSA -keystore awtest.jks -keysize 2048
 * </pre>
 * Zertifikat exportieren:
 * <pre>
 * keytool -export -keystore awtest.jks -alias awtest -file awtest.cer
 * </pre>
 * 
 * Das exportierte Zertifikat wird im Test eingelesen.
 * 
 * TODO: Von Java aus kann man nicht direkt auf den KeyStore zugreifen? Exportieren direkt aus Java? 
 * 
 * Hier:
 * <ul>
 *   <li>Lesen des private Keys aus dem KeyStore.</li>
 *   <li>Der public Key wird aus dem exportierten Zertifikat eingelesen.</li>
 * </ul>
 * 
 * @author winkler
 */
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
        
        PublicKey publicKeyFromFile = loadPublicKeyFromFile(ks, jksPassword);
        
        // Jwts.parser();
        
        JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
        Jws<Claims> x = parser.parseClaimsJws(compactJws);
        
        String id = x.getBody().getId();
        System.out.println("id: " + id);
        System.out.println("audience: " + x.getBody().getAudience());
        System.out.println("audience: " + x.getBody().getSubject());
        
        JwtParser parserFromFile = Jwts.parserBuilder().setSigningKey(publicKeyFromFile).build();
        Jws<Claims> x2 = parser.parseClaimsJws(compactJws);
        
        String id2 = x2.getBody().getId();
        System.out.println("id2: " + id2);
        System.out.println("audience: " + x2.getBody().getAudience());
        System.out.println("audience: " + x2.getBody().getSubject());                
    }
    
    public PublicKey loadPublicKeyFromFile(KeyStore keyStore, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        Key key = keyStore.getKey("awtest", password.toCharArray());
        if (key instanceof PrivateKey) {
            Certificate cert = keyStore.getCertificate("awtest");
            PublicKey publicKey = cert.getPublicKey();
            return publicKey;
        }
        return null;
    }

    public PublicKey loadPublicKey() throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(ReadKeyFromKeyStore.class.getResourceAsStream("awtest.cer"));
        PublicKey retVal = cert.getPublicKey();
        return retVal;
    }

}
