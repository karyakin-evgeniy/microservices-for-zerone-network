package org.proteam24.zeroneapplication.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class StorageConfig {
    @Value("${zerone.cloudinary.cloud-name}")
    private String cloudName;

    @Value("${zerone.cloudinary.api-key}")
    private String apiKey;

    @Value("${zerone.cloudinary.api-secret}")
    private String apiSecret;

    @Value("${zerone.cloudinary.secure}")
    private Boolean secure;


    @Bean
    public Cloudinary cloudinaryBean() {

        return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", secure));
    }
}
