package com.tfmunir.encryption_tool.service;

public interface IFileSecureCypher {

    public static final String ALGORITHM = "AES";

    String generateSecretKey();

    byte[] encrypFileBytes(byte[] fileBytes);

    byte[] decryptFileBytes(byte[] fileBytes);

    String generateUserVector();
}
