package com.southernwavebank.auth_service.jwt;

import java.nio.file.Files;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class JwtKeyStoreLoader {

    @Value("${jwt.keystore.path}")
    private Resource keystorePath;

    @Value("${jwt.keystore.password}")
    private String keystorePassword;

    @Value("${jwt.keystore.key-password}")
    private String keyPassword;

    private KeyStore keyStore;
    
    @Value("${jwt.publickey.path}")  // <-- auth-service.pem
    private Resource publicKeyPem;
    
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        keyStore = KeyStore.getInstance("JKS");
        keyStore.load(keystorePath.getInputStream(), keystorePassword.toCharArray());
        
     // Load public key from PEM
        this.publicKey = loadPublicKeyFromPem(publicKeyPem);
    }

    public PrivateKey getPrivateKey(String alias) throws Exception {
        Key key = keyStore.getKey(alias, keyPassword.toCharArray());
        if (key instanceof PrivateKey) {
        	 log.info("Loaded PrivateKey for alias '{}': Algorithm={}, Format={}", alias, key.getAlgorithm(), key.getFormat());
        	 return (PrivateKey) key;
        }
        throw new RuntimeException("No private key found for alias: " + alias);
    }

    
    private PublicKey loadPublicKeyFromPem(Resource pemResource) throws Exception {
        String pemContent = new String(Files.readAllBytes(pemResource.getFile().toPath()))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(pemContent);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }
}
