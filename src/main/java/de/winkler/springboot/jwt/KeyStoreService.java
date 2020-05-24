package de.winkler.springboot.jwt;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyStoreService {
    
    Logger logger = LoggerFactory.getLogger(KeyStoreService.class);

    @Value("${app.keystore.uripath}")
    private String keyStoreUriPath;

    @Value("${app.keystore.password}")
    private String keyStorePassword;

    @Value("${app.keystore.jwtcert}")
    private String keyStoreJwtCert;

    String getKeyStoreUriPath() {
        return keyStoreUriPath;
    }

    public Optional<PublicKey> publicKey() {
        Path path = Paths.get(URI.create(keyStoreUriPath));

        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            ks.load(Files.newInputStream(path), keyStorePassword.toCharArray());
            Key key = ks.getKey("awtest", keyStorePassword.toCharArray());

            if (key instanceof PrivateKey) {
                Certificate cert = ks.getCertificate(keyStoreJwtCert);
                PublicKey publicKey = cert.getPublicKey();
                return Optional.of(publicKey);
            }
            
            return Optional.empty();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | UnrecoverableKeyException ex) {
            
            logger.error("Unable to open and read defined keystore at location [{}].", keyStoreUriPath, ex);
            return Optional.empty();
        }
    }

}
