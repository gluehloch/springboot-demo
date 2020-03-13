package de.winkler.springboot.user;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.winkler.springboot.datetime.TimeService;

@Service
public class LoginService implements UserDetailsService {

    private static final String SPRING_DEMO_ISSUER = "SPRING_DEMO_ISSUER";
    private static final long EXPIRATION_DAYS = 3;
    private static final KeyPair KEY_PAIR;

    static {
        // TODO Auslagerung in eine Datei?!
        KEY_PAIR = Keys.keyPairFor(SignatureAlgorithm.RS256);
    }

    private final TimeService timeService;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(TimeService timeService, UserRepository userRepository) {
        this.timeService = timeService;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean login(String nickname, String password) {
        UserEntity user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException(
                    "Unknown user with nickname=[" + nickname + "].");
        }

        if (!user.getPassword().equals(password)) {
            // TODO Mit irgendwas signalisieren, dass der Login-Versuch nicht efolgreich war.
            return false;
        }

        return true;
    }

    public Token token(UserEntity user) {
        LocalDateTime tokenExpiration = timeService.now().plusDays(EXPIRATION_DAYS);

        String jws = Jwts.builder().setSubject(user.getNickname())
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

    public Optional<String> validate(String jwt) {
        Jws<Claims> jws;

        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(KEY_PAIR.getPublic())
                    .build()
                    .parseClaimsJws(jwt);

            // String x = jws.getBody().getIssuer();
            String nickname = jws.getBody().getSubject();
            return Optional.of(nickname);
        } catch (JwtException ex) {
            return Optional.empty();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByNickname(username);
        if (user == null) {
            throw new UsernameNotFoundException("Unknown user with nickname=[" + username + "].");
        }

        return new AWUserDetails(user.getNickname(), user.getPassword());
    }

}
