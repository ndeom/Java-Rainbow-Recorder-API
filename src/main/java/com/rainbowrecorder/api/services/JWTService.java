package com.rainbowrecorder.api.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rainbowrecorder.api.models.User;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JWTService {

    private String secret;

    public JWTService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public Optional<String> createToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withClaim("user_id", user.getUser_id())
                    .withClaim("username", user.getUsername())
                    .withClaim("profilePicture", user.getProfile_picture())
                    .withClaim("screenName", user.getScreen_name())
                    .sign(algorithm);
            return Optional.ofNullable(token);
        }
        catch(JWTCreationException err) {
            return Optional.empty();
        }
    }

    public HttpHeaders setAndReturnAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Access-Control-Expose-Headers", "Authorization");
        return headers;
    }


    public Optional<String> getIdFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            String id = verifier.verify(token).getClaim("user_id").asString();
            return Optional.ofNullable(id);
        } catch(Exception err) {
            return Optional.empty();
        }
    }

    public Boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch(JWTVerificationException err) {
            return false;
        }
    }

}
