package ru.simankin.aspect.logging.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.simankin.aspect.logging.aspect.LoggingAspect;

@Configuration
public class LoggingConfiguration {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
