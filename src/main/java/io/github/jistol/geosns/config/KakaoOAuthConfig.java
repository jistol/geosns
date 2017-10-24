package io.github.jistol.geosns.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import javax.servlet.Filter;

@Configuration
public class KakaoOAuthConfig {
    @Value("${kakao.oauth.login-complete-url}") private String loginCompleteUrl;
    @Value("${kakao.oauth.login-url}") private String loginUrl;

    @Autowired private OAuth2ClientContext oAuth2ClientContext;

    @Bean
    @Qualifier("kakaoResourceDetail")
    @ConfigurationProperties("kakao.oauth.client")
    public AuthorizationCodeResourceDetails kakaoResourceDetail() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @Qualifier("kakaoResourceProp")
    @ConfigurationProperties("kakao.oauth.resource")
    public ResourceServerProperties kakaoResourceProp() {
        return new ResourceServerProperties();
    }

    @Bean
    @Qualifier("kakaoOAuthFilter")
    public Filter kakaoOAuthFilter(@Qualifier("kakaoResourceDetail") AuthorizationCodeResourceDetails kakaoResourceDetail,
                                   @Qualifier("kakaoResourceProp") ResourceServerProperties kakaoResourceProp) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(loginUrl);
        OAuth2RestTemplate template = new OAuth2RestTemplate(kakaoResourceDetail, oAuth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(kakaoResourceProp.getUserInfoUri(), kakaoResourceDetail.getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> response.sendRedirect(loginCompleteUrl));
        filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error"));
        return filter;
    }
}
