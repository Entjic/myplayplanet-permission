package net.myplayplanet.permission.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(
        scanBasePackages = {"net.myplayplanet.permission"}
)
@EnableWebMvc
@ConfigurationPropertiesScan("net.myplayplanet.permission.service.config")
public class PermissionSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionSystemApplication.class, args);
    }

}
