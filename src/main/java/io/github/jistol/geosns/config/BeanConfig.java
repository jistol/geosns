package io.github.jistol.geosns.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Configuration
public class BeanConfig {
    @Value("${post.load-limit}") private Integer limit;

    @Bean
    @Qualifier("postLimit")
    public Pageable postLimit() {
        return new PageRequest(0, limit);
    }
}
