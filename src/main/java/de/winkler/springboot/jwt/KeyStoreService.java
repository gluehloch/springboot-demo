package de.winkler.springboot.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.cert.Certificate;
import java.util.Optional;

import jakarta.annotation.PostConstruct;

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

    @Value("${app.keystore.alias}")
    private String keyStoreAlias;

    Resource getKeyStoreResource() {
        return keyStoreResource;
    }

    private KeyStore ks;
    private Key key;
    
    Key getKey() {
        return key;
    }
    
    KeyStore getKeyStore() {
        return ks;
    }

    @PostConstruct
    public void init() throws Exception {
        LOG.info("Expecting KeyStore: {}", keyStoreResource.getFilename());
        ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(keyStoreResource.getInputStream(), keyStorePassword.toCharArray());
        key = ks.getKey(keyStoreAlias, keyStorePassword.toCharArray());
    }

    /**
     * Returns the public key to validate a JWT.
     * 
     * @return public key
     */
    public Optional<PublicKey> publicKey() {
        try {
            if (key instanceof PrivateKey) {
                Certificate cert = ks.getCertificate(keyStoreAlias);
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
        Key key;
        try {
            key = ks.getKey(keyStoreAlias, keyStorePassword.toCharArray());
        } catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException ex) {
            LOG.error("Unable to open and read defined keystore at location [{}].", keyStoreResource, ex);
            return Optional.empty();
        }

        return Optional.ofNullable(key);
    }

}
