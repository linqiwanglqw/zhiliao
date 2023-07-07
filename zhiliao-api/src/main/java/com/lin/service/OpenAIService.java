package com.lin.service;

import java.io.IOException;

public interface OpenAIService {
    String doChatGPT(String question) throws IOException;

    void generateAnswers(String content, Long id, Boolean isEdit) throws IOException;
}
