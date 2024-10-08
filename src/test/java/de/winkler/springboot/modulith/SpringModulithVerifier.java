package de.winkler.springboot.modulith;

import de.winkler.springboot.RestdemoApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import org.springframework.modulith.docs.Documenter;

class SpringModulithVerifier {

    @Test
    void modulithChecker() {
        // TODO Welche Dependency muss ich mir ziehen?
        final var modules = ApplicationModules.of(RestdemoApplication.class).verify();
        new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
    }
}
