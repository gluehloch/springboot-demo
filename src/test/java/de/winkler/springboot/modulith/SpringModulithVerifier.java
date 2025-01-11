package de.winkler.springboot.modulith;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

import de.winkler.springboot.RestdemoApplication;

class SpringModulithVerifier {

    @Test
    void modulithChecker() {
        // TODO Welche Dependency muss ich mir ziehen?
        final var modules = ApplicationModules.of(RestdemoApplication.class).verify();
        modules.forEach(System.out::println);
        new Documenter(modules).writeModulesAsPlantUml().writeIndividualModulesAsPlantUml();
    }
}
