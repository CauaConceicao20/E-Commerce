package com.compass.e_commerce.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.compass.e_commerce.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.private.key}")
    private String privateKeyPath;

    @Value("${api.security.public.key}")
    private String publicKeyPath;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
             loadKeys(privateKeyPath, publicKeyPath);
        } catch (Exception e) {
            throw new RuntimeException("Error loading private key", e);
        }
    }

    public void loadKeys(String privateKeyPath, String publicKeyPath) throws Exception {
        privateKey = loadPrivateKey(privateKeyPath);
        publicKey = loadPublicKey(publicKeyPath);
    }

    public RSAPrivateKey loadPrivateKey(String privateKeyPath) throws Exception {
        FileInputStream privateKeyInputStream = new FileInputStream(new File(privateKeyPath));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyInputStream.readAllBytes()));
        return privateKey;
    }

    public RSAPublicKey loadPublicKey(String publicKeyPath) throws Exception {
        FileInputStream publicKeyInputStream = new FileInputStream(new File(publicKeyPath));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyInputStream.readAllBytes()));
        return publicKey;
    }

    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            String token = JWT.create()
                    .withIssuer("E-commerce")
                    .withSubject(user.getUsername())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating token", exception);
        }
    }

    public String generateTokenResetPassword(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("E-commerce")
                    .withSubject(user.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating token", exception);
        }
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
            return JWT.require(algorithm)
                    .withIssuer("E-commerce")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception){
           throw new JWTVerificationException("Token inválido ou expirado", exception);
        }
    }

    private Instant generateExpirationDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
