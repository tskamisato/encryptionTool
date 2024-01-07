package com.tfmunir.encryption_tool.service.impl;

import com.tfmunir.encryption_tool.model.ToolUser;
import com.tfmunir.encryption_tool.repository.ToolUserRepository;
import com.tfmunir.encryption_tool.service.IFileSecureCypher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileSecureCypherImpl implements IFileSecureCypher {

    private final ToolUserRepository repository;

    @Override
    public String generateSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        keyGenerator.init(256);
        return encodeKeyToString(keyGenerator.generateKey());
    }

    @Override
    public byte[] encrypFileBytes(byte[] fileBytes) {

        ToolUser toolUser = getToolUser();
        SecretKey secretKey = decodeStringToKey(toolUser.getUserKey());
        byte[] iv = Base64.getDecoder().decode(toolUser.getUserVector());

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Configurar el cifrador con el modo GCM y el vector de inicializacion
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            return cipher.doFinal(fileBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public byte[] decryptFileBytes(byte[] fileBytes) {

        ToolUser toolUser = getToolUser();
        SecretKey secretKey = decodeStringToKey(toolUser.getUserKey());
        byte[] iv = Base64.getDecoder().decode(toolUser.getUserVector());

        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Configurar el cifrador con el modo GCM y el vector de inicializacion
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            return cipher.doFinal(fileBytes);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                 BadPaddingException | InvalidAlgorithmParameterException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public String generateUserVector() {

        // Generamos un vector de inicializacion aleatorio
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[12]; // 96 bits
        random.nextBytes(iv);

        return Base64.getEncoder().encodeToString(iv);
    }

    private ToolUser getToolUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<ToolUser> authUser = repository.findByUsername(authentication.getName());

        if (authUser.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return authUser.get();
    }

    private String encodeKeyToString(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    private SecretKey decodeStringToKey(String keyString) {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new javax.crypto.spec.SecretKeySpec(keyBytes, "AES");
    }
}
