package de.winkler.springboot.security;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import de.winkler.springboot.datetime.TimeService;
import de.winkler.springboot.user.PrivilegeEntity;
import de.winkler.springboot.user.PrivilegeRepository;
import de.winkler.springboot.user.RoleRepository;
import de.winkler.springboot.user.Token;
import de.winkler.springboot.user.UserEntity;
import de.winkler.springboot.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

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
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;

    @Autowired
    public LoginService(TimeService timeService, UserRepository userRepository, RoleRepository roleRepository,
            PrivilegeRepository privilegeRepository) {

        this.timeService = timeService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
    }

    @Transactional
    public boolean login(String nickname, String password) {
        UserEntity user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new EntityNotFoundException("Unknown user with nickname=[" + nickname + "]."));

        if (!user.getPassword().equals(password)) {
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
        final UserEntity user = userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unknown user with nickname=[" + username + "]."));

        AWUserDetails.AWUserDetailsBuilder userDetailsBuilder = AWUserDetails.AWUserDetailsBuilder
                .of(user.getNickname(), user.getPassword());

        Set<PrivilegeEntity> privileges = privilegeRepository.findByNickname(user.getNickname());

        for (PrivilegeEntity privilege : privileges) {
            userDetailsBuilder.addGrantedAuthority(new SimpleGrantedAuthority(privilege.getName()));
        }

        return userDetailsBuilder.build();
    }

}
