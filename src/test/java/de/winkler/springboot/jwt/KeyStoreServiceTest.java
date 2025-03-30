package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KeyStoreServiceTest {

    @Autowired
    private KeyStoreService keyStoreService;

    @DisplayName("Read a key from a Java KeyStore file.")
    @Tag("keystore")
    @Test
    void readCert() {
        assertThat(keyStoreService.getKeyStoreResource()).isNotNull();
        assertThat(keyStoreService.getClass()).isEqualTo(KeyStoreService.class);
        assertThat(keyStoreService.getKey()).isNotNull();
    }
    
}
