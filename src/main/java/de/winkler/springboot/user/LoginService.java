package de.winkler.springboot.user;

import java.security.Key;

import javax.transaction.Transactional;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;

    @Autowired
    public LoginService(UserRepository userRepository) {
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

        // TODO Der Key wird eigentlich nur einmal generiert.
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject(nickname).signWith(key).compact();

        return new Token(jws);
    }

    @Transactional
    public Token logout(Token token) {
        // TODO Invalidate session.
        return null;
    }

}
