package io.github.jistol.geosns.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Profile({"redis"})
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {
    @Value("${spring.redis.host}") private String redisHost;
    @Value("${spring.redis.port}") private int redisPort;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        JedisConnectionFactory conn = new JedisConnectionFactory();
        conn.setHostName(redisHost);
        conn.setPort(redisPort);
        //conn.setDatabase(database);
        conn.setUsePool(true);
        return conn;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        // 위 레디스 처럼 serializer 의 각종 설정 가능.
        // tomcat context 로 설정한 쿠키 기능들도 여기서 설정가능.
        return serializer;
    }

    /**
     * for AWS Error
     * @return
     */
    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
