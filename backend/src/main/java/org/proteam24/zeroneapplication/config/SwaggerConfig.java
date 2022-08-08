package org.proteam24.zeroneapplication.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Zerone network API", version = "1.0"))
public class SwaggerConfig {


    @Bean
    public OpenAPI zeroneOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                        .name("Authorization")
                                        .scheme("bearer")
                                        .$ref("")
                                        .description("")
                                        .bearerFormat("token")
                                        .in(SecurityScheme.In.HEADER)))
                .info(new io.swagger.v3.oas.models.info.Info()
                        .contact(new Contact()
                                .email("karykin.ea@gmail.com")
                                .name("Karyakin Evgenii")
                        )
                );
    }

}
