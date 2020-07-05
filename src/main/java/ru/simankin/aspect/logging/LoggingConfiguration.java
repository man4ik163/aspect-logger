package ru.simankin.aspect.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
