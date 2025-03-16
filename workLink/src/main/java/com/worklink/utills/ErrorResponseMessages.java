package com.worklink.utills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = "classpath:message.properties")
public class ErrorResponseMessages {

    @Autowired
    private Environment environment;

    public String getMessage(String key) {
        return environment.getProperty(key);
    }
}
