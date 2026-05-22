package com.avocadogroup.recipy.common.configs;

import com.cloudinary.Cloudinary;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.cloudinary")
@Data
public class CloudinaryConfig {
    // Cloudinary Url
    private String url;

    @Bean // Means that it will be registered as a Spring bean and can be injected into other parts of the application.
    public Cloudinary cloudinary() {
        // Create and return the Cloudinary object which is the entry point of the library
        return new Cloudinary(url);
    }
}
