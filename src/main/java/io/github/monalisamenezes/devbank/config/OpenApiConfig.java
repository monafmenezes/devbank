package io.github.monalisamenezes.devbank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devbankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Devbank API")
                        .description("API REST simplificada para transferência de fundos e consulta de movimentações entre contas")
                        .version("v1"));
    }
}
