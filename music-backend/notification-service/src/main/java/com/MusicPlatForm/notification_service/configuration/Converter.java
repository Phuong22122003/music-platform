package com.MusicPlatForm.notification_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.converter.JsonMessageConverter;

@Configuration
public class Converter {
    
    @Bean
    JsonMessageConverter MyConverter() {
        return new JsonMessageConverter();
    }
}
