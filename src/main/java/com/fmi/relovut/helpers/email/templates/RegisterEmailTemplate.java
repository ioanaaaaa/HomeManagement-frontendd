package com.fmi.relovut.helpers.email.templates;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
@Component
public class RegisterEmailTemplate {
    public RegisterEmailTemplate(@Value("classpath:templates/register-template.html") Resource registerEmailTemplateFile) throws IOException {
        // Register Email Template
        InputStream stream = registerEmailTemplateFile.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        this.template = reader.lines().collect(Collectors.joining("\n"));
    }

    private final String template;
}
