package de.winkler.springboot.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyStoreService {

    @Value("${app.keystore.file}")
    private String keyStoreFilePath;
    
    String getKeyStoreFilePath() {
        return keyStoreFilePath;
    }
    
}
