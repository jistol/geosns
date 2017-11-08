package io.github.jistol.geosns.config;

import io.github.jistol.geosns.type.BaseProps;
import io.github.jistol.geosns.util.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Configuration
public class BeanConfig {
    @Value("${post.load-limit}") private Integer limit;
    @Autowired private BaseProps baseProps;

    @Bean
    @Qualifier("postLimit")
    public Pageable postLimit() {
        return new PageRequest(0, limit);
    }

    @Bean
    @Qualifier("baseCrypt")
    public Crypt baseCrypt() throws Exception {
        return new Crypt(baseProps.getKey(), baseProps.getIv());
    }
}
