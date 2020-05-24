package de.winkler.springboot.jwt;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Defines KeyStore access to get public/private keys to create and validate the
 * JWT.
 * 
 * @author Andre Winkler
 */
@Component
public class KeyStoreService {

    private static final Logger LOG = LoggerFactory.getLogger(KeyStoreService.class);

    @Value("${app.keystore.uripath}")
    private Resource keyStoreResource;

    @Value("${app.keystore.password}")
    private String keyStorePassword;

    @Value("${app.keystore.jwtcert}")
    private String keyStoreJwtCert;

    Resource getKeyStoreResource() {
        return keyStoreResource;
    }

    private KeyStore ks;
    private Key key;

    @PostConstruct
    public void init() throws Exception {
        ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
        key = ks.getKey("awtest", keyStorePassword.toCharArray());
    }

    /**
     * Returns the public key to validate a JWT.
     * 
     * @return public key
     */
    public Optional<PublicKey> publicKey() {
        try {
            if (key instanceof PrivateKey) {
                Certificate cert = ks.getCertificate(keyStoreJwtCert);
                PublicKey publicKey = cert.getPublicKey();
                return Optional.of(publicKey);
            }

            return Optional.empty();
        } catch (KeyStoreException ex) {
            LOG.error("Unable to open and read defined keystore at location [{}].", keyStoreResource, ex);
            return Optional.empty();
        }
    }

    /**
     * Returns the private key to create a JWT.
     * 
     * @return private key
     */
    public Optional<Key> privateKey() {
        try {
            return Optional.ofNullable(ks.getKey("awtest", keyStorePassword.toCharArray()));
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
            LOG.error("Unable to open and read defined keystore at location [{}].", keyStoreResource, ex);
            return Optional.empty();
        }
    }

}
