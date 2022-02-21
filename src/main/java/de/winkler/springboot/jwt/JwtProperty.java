package de.winkler.springboot.jwt;

import java.time.LocalDateTime;
import java.util.List;

public record JwtProperty(String subject,
                          String issuer,
                          LocalDateTime issuedAt,
                          LocalDateTime expiration,
                          List<String> claims) {

}
