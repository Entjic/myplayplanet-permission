package net.myplayplanet.permission.service.config;

import com.google.inject.AbstractModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.guice.annotation.EnableGuiceModules;

@Configuration
@EnableGuiceModules
public class GuiceModuleConfiguration {

    @Bean
    public static AbstractModule example(){
        return new AbstractModule(){
            @Override
            protected void configure() {
                super.configure();
            }
        };
    };

}
