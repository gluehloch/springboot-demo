package de.winkler.springboot.modulith;

import de.winkler.springboot.RestdemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

class SpringModulithVerifier {

    @Test
    void modulithChecker() {
        // TODO Welche Dependency muss ich mir ziehen?
        ApplicationModules.of(RestdemoApplication.class).verify();
    }
}
