package com.example.taskflowapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskFlowOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TaskFlow API REST")
                        .description("API de gestion de tâches style Trello simplifié - Formation DevSecOps & CI/CD")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Équipe DHI Academy")
                                .email("contact@dhi-academy.com")
                                .url("https://dhi-academy.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
