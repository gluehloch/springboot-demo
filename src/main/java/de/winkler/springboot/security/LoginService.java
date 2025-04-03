package de.winkler.springboot.security;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.transaction.Transactional;

import de.winkler.springboot.datetime.TimeService;
import de.winkler.springboot.jwt.JwtGenerator;
import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.UserCredentials;
import de.winkler.springboot.user.UserService;
import de.winkler.springboot.user.internal.PrivilegeEntity;
import de.winkler.springboot.user.internal.PrivilegeRepository;

@Service
public class LoginService implements UserDetailsService {

    private static final String SPRING_DEMO_ISSUER = "SPRING_DEMO_ISSUER";
    private static final long EXPIRATION_DAYS = 3;
    private static final KeyPair KEY_PAIR;

    static {
        // TODO Auslagerung in eine Datei?! Mittel KeyStoreService auslesen.
        KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    private final JwtGenerator jwtGenerator;
    private final TimeService timeService;
    private final UserService userService;
    private final PrivilegeRepository privilegeRepository;

    public LoginService(TimeService timeService, UserService userService, JwtGenerator jwtGenerator, PrivilegeRepository privilegeRepository) {
        this.jwtGenerator = jwtGenerator;
        this.timeService = timeService;
        this.userService = userService;
        this.privilegeRepository = privilegeRepository;
    }

    @Transactional
    public boolean login(Nickname nickname, String password) {
        if (!userService.validatePassword(nickname, password)) {
            // TODO Mit irgendwas signalisieren, dass der Login-Versuch nicht efolgreich war.
            return false;
        }

        return true;
    }

    public Token token(UserDetails user) {
        LocalDateTime tokenExpiration = timeService.now().plusDays(EXPIRATION_DAYS);

        String jws = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuer(SPRING_DEMO_ISSUER)
                .setIssuedAt(timeService.currently())
                .setExpiration(TimeService.convertToDateViaInstant(tokenExpiration))
                .signWith(KEY_PAIR.getPrivate())
                .compact();

        return new Token(jws);
    }

    @Transactional
    public Token logout(Token token) {
        // TODO Invalidate session.
        return null;
    }

    public Optional<Nickname> validate(String jwt) {
        return LoginService.tryGet(jwt, token -> Jwts.parserBuilder()
                .setSigningKey(KEY_PAIR.getPublic())
                .build()
                .parseClaimsJws(token)).map(claims -> Nickname.of(claims.getBody().getSubject()));
    }

    public static Optional<Jws<Claims>> tryGet(String jwt, Function<String, Jws<Claims>> tokenParser) {
        try {
            Jws<Claims> jws = tokenParser.apply(jwt);
            return Optional.of(jws);
        } catch (JwtException ex) {
            return Optional.empty();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserCredentials user = userService.findByNickname(Nickname.of(username))
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user with nickname=[" + username + "]."));

        AWUserDetails.AWUserDetailsBuilder userDetailsBuilder = AWUserDetails.AWUserDetailsBuilder
                .of(user.nickname(), user.password());

        Set<PrivilegeEntity> privileges = privilegeRepository.findByNickname(user.nickname());

        for (PrivilegeEntity privilege : privileges) {
            userDetailsBuilder.addGrantedAuthority(new SimpleGrantedAuthority(privilege.getName()));
        }

        return userDetailsBuilder.build();
    }

}
