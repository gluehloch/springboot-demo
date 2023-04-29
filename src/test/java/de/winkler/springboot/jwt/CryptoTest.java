package de.winkler.springboot.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CryptoTest {

    private final KeyStoreService keyStoreService;

    @Autowired
    public CryptoTest(KeyStoreService keyStoreService) {
        this.keyStoreService = keyStoreService;
    }

    @Test
    void encodeAndDecode() throws Exception {
        CryptoUtil cryptoUtil = new CryptoUtil(keyStoreService);
        String encryptedString = "This a test for me.";
        byte[] encrypted = cryptoUtil.encrypt(encryptedString.getBytes());
        byte[] decryptedData = cryptoUtil.decrypt(encrypted);
        String decrytpedstring = new String(decryptedData);
        assertThat(encryptedString).isEqualTo(decrytpedstring);
    }

}
