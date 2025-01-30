package net.myplayplanet.permission.service.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI api() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

//        Contact contact = new Contact();
//        contact.setName("MyPlayPlanet Development");
//        contact.setUrl("myplayplanet.net");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Permission API")
                .version("1.0.0")
                // .contact(contact)
                .description("This API exposes endpoints to manage and retrieve users, permissions and groups.")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(server));
    }

}

