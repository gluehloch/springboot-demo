package de.winkler.springboot.user;

import java.security.Key;
import java.time.LocalDateTime;

import javax.transaction.Transactional;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.winkler.springboot.datetime.TimeService;

@Service
public class LoginService {

    private static final String SPRING_DEMO_ISSUER = "SPRING_DEMO_ISSUER";
    private static final long EXPIRATION_DAYS = 3;
    private static final Key KEY;

    static {
        // TODO Auslagerung in eine Datei?!
        KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    private final TimeService timeService;
    private final UserRepository userRepository;

    @Autowired
    public LoginService(TimeService timeService, UserRepository userRepository) {
        this.timeService = timeService;
        this.userRepository = userRepository;
    }

    @Transactional
    public Token login(String nickname, String password) {
        UserEntity user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new IllegalArgumentException(
                    "Unknown user with nickname=[" + nickname + "].");
        }

        if (!user.getPassword().equals(password)) {
            // TODO Mit irgendwas signalisieren, dass der Login-Versuch nicht efolgreich war.
            return null;
        }

        LocalDateTime tokenExpiration = timeService.now().plusDays(EXPIRATION_DAYS);

        String jws = Jwts.builder().setSubject(nickname)
                .setIssuer(SPRING_DEMO_ISSUER)
                .setIssuedAt(timeService.currently())
                .setExpiration(TimeService.convertToDateViaInstant(tokenExpiration))
                .signWith(KEY)
                .compact();

        return new Token(jws);
    }

    @Transactional
    public Token logout(Token token) {
        // TODO Invalidate session.
        return null;
    }

    public boolean validate(Token token) {
        Jws<Claims> jws;

        try {
            jws = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token.getContent());

            String x = jws.getBody().getIssuer();
            return true;
        } catch (JwtException ex) {
            // we *cannot* use the JWT as intended by its creator
            return false;
        }
    }

}
