package gle.carpoolspring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Hibernate6Module hibernateModule = new Hibernate6Module();
        // This ensures that no lazy-loaded objects are forcibly initialized.
        // It prevents trying to serialize uninitialized proxies.
        hibernateModule.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);

        mapper.registerModule(hibernateModule);

        return mapper;
    }
}