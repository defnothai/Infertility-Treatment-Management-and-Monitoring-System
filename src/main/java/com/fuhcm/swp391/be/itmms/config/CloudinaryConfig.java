package com.fuhcm.swp391.be.itmms.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dcwg1jda0",
                "api_key", "161474556811557",
                "api_secret", "Q7Cn5dYwyX-ESQPrLKTsog0QwP0"
        ));
    }
}
