package ir.asta.training.warehouse.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

@Configuration
public class BeanConfig {

    @Bean
    public Client getClient() {
        return ClientBuilder.newClient();
    }
}
