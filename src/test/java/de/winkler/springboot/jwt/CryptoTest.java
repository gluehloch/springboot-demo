package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

@SpringBootTest
class CryptoTest {

    private final KeyStoreService keyStoreService;

    @Autowired
    CryptoTest(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    @Test
    void encodeAndDecode() throws Exception {
        Cryptonium cryptoUtil = new Cryptonium(keyStoreService);
        String encryptedString = "This a test for me.";
        byte[] encrypted = cryptoUtil.encrypt(encryptedString.getBytes(StandardCharsets.UTF_8));
        byte[] decryptedData = cryptoUtil.decrypt(encrypted);
        String decrytpedstring = new String(decryptedData);
        assertThat(encryptedString).isEqualTo(decrytpedstring);
    }

}
