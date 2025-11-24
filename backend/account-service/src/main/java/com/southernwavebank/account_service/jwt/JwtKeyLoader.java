package com.southernwavebank.account_service.jwt;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class JwtKeyLoader {

	@Value("${jwt.publickey.path}") 
    private Resource publicKeyPem;

    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        String pemContent = new String(Files.readAllBytes(publicKeyPem.getFile().toPath()))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(pemContent);
        this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}

