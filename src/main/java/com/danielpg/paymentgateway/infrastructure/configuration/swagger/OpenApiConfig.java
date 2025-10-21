package com.danielpg.paymentgateway.infrastructure.configuration.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        tags = {
                @Tag(name = "01 - Usuários", description = "Cadastro de usuários."),
                @Tag(name = "02 - Autenticação", description = "Autenticação/login de acesso."),
                @Tag(name = "03 - Depósitos", description = "Gerenciamento de depósitos."),
                @Tag(name = "04 - Cobranças", description = "Gerenciamento de cobranças."),
                @Tag(name = "05 - Pagamentos", description = "Gerenciamento de pagamentos."),
                @Tag(name = "06 - Monitoramento", description = "Monitoramento do estado do serviço.")
        }
)
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .components(new Components()
                                .addResponses("AppErrorResponse",
                                        new ApiResponse()
                                                .description("Erro genérico")
                                                .content(new Content()
                                                        .addMediaType("application/json", new MediaType()
                                                                .schema(new Schema<>()
                                                                        .$ref("#/components/schemas/AppErrorResponse")
                                                                )
                                                        )
                                                )
                                )
                        );
        }
}