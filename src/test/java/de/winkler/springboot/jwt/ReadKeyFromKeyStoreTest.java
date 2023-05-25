package de.winkler.springboot.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 *
 * KeyStore erstellen:
 * 
 * <pre>
 * keytool -genkey -alias awtest -keyalg RSA -keystore awtest.jks -keysize 2048
 * </pre>
 * 
 * Zertifikat exportieren:
 * 
 * <pre>
 * keytool -export -keystore awtest.jks -alias awtest -file awtest.cer
 * </pre>
 * 
 * Das exportierte Zertifikat wird im Test eingelesen.
 * 
 * TODO: Von Java aus kann man nicht direkt auf den KeyStore zugreifen?
 * Exportieren direkt aus Java?
 * 
 * Hier:
 * <ul>
 * <li>Lesen des private Keys aus dem KeyStore.</li>
 * <li>Der public Key wird aus dem exportierten Zertifikat eingelesen.</li>
 * </ul>
 * 
 * @author winkler
 */
@SpringBootTest
class ReadKeyFromKeyStoreTest {

    private static final Logger LOG = LoggerFactory.getLogger(ReadKeyFromKeyStoreTest.class);
 
    @Autowired
    private KeyStoreService keyStoreService;

    @DisplayName("Read a key from a Java KeyStore file.")
    @Tag("keystore")
    @Test
    void readKey() throws Exception {
        Key key = keyStoreService.privateKey().orElseThrow();

        assertThat(key).isNotNull();

        String compactJws = Jwts.builder()
                .setSubject("Joe")
                .setAudience("testAudienceId")
                .setExpiration(toDate(LocalDateTime.now().plusDays(3)))
                .setIssuedAt(toDate(LocalDateTime.now()))
                .setId("testuserid")
                .signWith(key /* , SignatureAlgorithm.RS512 */)
                .compact();

        LOG.info("JWT: {}", compactJws);

        PublicKey publicKey = keyStoreService.publicKey().orElseThrow();

        JwtParser parser = Jwts.parserBuilder().setSigningKey(publicKey).build();
        Jws<Claims> x = parser.parseClaimsJws(compactJws);

        String id = x.getBody().getId();
        LOG.info("id: " + id);
        LOG.info("audience: " + x.getBody().getAudience());
        LOG.info("audience: " + x.getBody().getSubject());

        PublicKey publicKeyFromFile = loadPublicKeyFromFile(keyStoreService.getKeyStore(), "awtest666");
        JwtParser parserFromFile = Jwts.parserBuilder().setSigningKey(publicKeyFromFile).build();
        Jws<Claims> x2 = parserFromFile.parseClaimsJws(compactJws);

        String id2 = x2.getBody().getId();
        LOG.info("id2: " + id2);
        LOG.info("audience: " + x2.getBody().getAudience());
        LOG.info("audience: " + x2.getBody().getSubject());
    }
    
    @Test
    void createJwtWithPublicKeyWillFail() {
    	PublicKey publicKey = keyStoreService.publicKey().orElseThrow();
    	
        assertThat(publicKey).isNotNull();

        String compactJws = null;
        try {
	        compactJws = Jwts.builder()
	                .setSubject("Joe")
	                .setAudience("testAudienceId")
	                .setExpiration(toDate(LocalDateTime.now().plusDays(3)))
	                .setIssuedAt(toDate(LocalDateTime.now()))
	                .setId("testuserid")
	                .signWith(publicKey /* , SignatureAlgorithm.RS512 */)
	                .compact();
        } catch (Exception ex) {
        	// fail("Creating a JWT with a public key should fail.");
        	// But it does not fail...
        }
        
        LOG.info("JWT: {}", compactJws);
    }

    PublicKey loadPublicKeyFromFile(KeyStore keyStore, String password)
            throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {

        Key key = keyStore.getKey("awtest", password.toCharArray());
        if (key instanceof PrivateKey) {
            Certificate cert = keyStore.getCertificate("awtest");
            PublicKey publicKey = cert.getPublicKey();
            return publicKey;
        }
        return null;
    }

    PublicKey loadPublicKey() throws CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(ReadKeyFromKeyStoreTest.class.getResourceAsStream("awtest.cer"));
        PublicKey retVal = cert.getPublicKey();
        return retVal;
    }

    private LocalDateTime toDate(Date date) {
        return date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    private Date toDate(LocalDateTime dateTime) {
        return java.util.Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
