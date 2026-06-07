package com.sep.psp.back.shared.service.interf;

public interface ApiKeyGeneratorService {

    String generateApiKey(String prefix, String alphabet, int length);

}