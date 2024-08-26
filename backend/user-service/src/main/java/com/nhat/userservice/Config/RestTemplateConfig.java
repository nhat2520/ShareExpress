package com.nhat.userservice.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

public class RestTemplateConfig {
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
