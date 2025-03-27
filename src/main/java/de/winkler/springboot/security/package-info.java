@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"user", "user :: RoleRepository"}
)
package de.winkler.springboot.security;