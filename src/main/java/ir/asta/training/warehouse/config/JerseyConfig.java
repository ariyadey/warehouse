package ir.asta.training.warehouse.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@Component
@ApplicationPath("warehouse/api")
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("ir.asta.training.warehouse.service");
    }
}
