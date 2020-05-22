package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ReadKeyFromKeyStore {

    @DisplayName("Read a key from a Java KeyStore file.")
    @Tag("keystore")
    @Test
    public void readKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
            FileNotFoundException, IOException, UnrecoverableKeyException {

        String jksPassword = "awtest666";

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        ks.load(ReadKeyFromKeyStore.class.getResourceAsStream("awtest.jks"), jksPassword.toCharArray());
        Key key = ks.getKey("awtest", jksPassword.toCharArray());

        assertThat(key).isNotNull();
    }

}
