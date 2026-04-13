package br.com.tonspersonalizados.usuarios_ms.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API usuários",
                description = "API para gerenciamento de usuários e funcionários",
                version = "1.0"
        )
)@SecurityScheme(name = "Bearer", type = SecuritySchemeType.HTTP, scheme = "Bearer", bearerFormat = "JWT")
public class OpenApiConfig {
}
