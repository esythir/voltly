package br.com.fiap.voltly.config.security;

import br.com.fiap.voltly.domain.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${security.jwt.secret}")
    private String secret;

    public String generateToken(User user){
        try{
            Algorithm alg = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("voltly-api")
                    .withSubject(user.getEmail())
                    .withExpiresAt(expiration())
                    .sign(alg);
        }catch (JWTCreationException e){
            throw new RuntimeException("Unable to generate JWT", e);
        }
    }

    public String validateToken(String token){
        try{
            Algorithm alg = Algorithm.HMAC256(secret);
            return JWT.require(alg)
                    .withIssuer("voltly-api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return null;
        }
    }

    private Instant expiration(){
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-03:00"));
    }
}
