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

import java.util.ArrayList;
import java.util.Collection;

import static io.github.jistol.geosns.util.Cast.list;
import static io.github.jistol.geosns.util.Cast.msgFormat;

@Configuration
public class OAuthBeanConfig {
    @Value("${oauth.login-complete-url}") private String loginCompleteUrl;
    @Value("${oauth.login-url}") private String loginUrl;

    @Autowired private OAuth2ClientContext oAuth2ClientContext;

    @Autowired @Qualifier("kakaoOAuthFilter") private Filter kakaoOAuthFilter;
    @Autowired @Qualifier("googleOAuthFilter") private Filter googleOAuthFilter;
    @Autowired @Qualifier("facebookOAuthFilter") private Filter facebookOAuthFilter;

    @Bean
    @Qualifier("socialOAuthFilterList")
    public Collection<Filter> socialOAuthFilterList() {
        return list(googleOAuthFilter, facebookOAuthFilter, kakaoOAuthFilter);
    }

    @Bean
    @Qualifier("kakaoOAuthFilter")
    public Filter kakaoOAuthFilter(@Qualifier("kakaoResourceDetail") AuthorizationCodeResourceDetails details,
                                   @Qualifier("kakaoResourceProp") ResourceServerProperties resourceProp) {
        return getOAuthFilter("kakao", details, resourceProp);
    }

    @Bean
    @Qualifier("googleOAuthFilter")
    public Filter googleOAuthFilter(@Qualifier("googleResourceDetail") AuthorizationCodeResourceDetails details,
                                   @Qualifier("googleResourceProp") ResourceServerProperties resourceProp) {
        return getOAuthFilter("google", details, resourceProp);
    }

    @Bean
    @Qualifier("facebookOAuthFilter")
    public Filter facebookOAuthFilter(@Qualifier("facebookResourceDetail") AuthorizationCodeResourceDetails details,
                                    @Qualifier("facebookResourceProp") ResourceServerProperties resourceProp) {
        return getOAuthFilter("facebook", details, resourceProp);
    }

    @Bean
    @Qualifier("kakaoResourceDetail")
    @ConfigurationProperties("oauth.kakao.client")
    public AuthorizationCodeResourceDetails kakaoResourceDetail() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @Qualifier("kakaoResourceProp")
    @ConfigurationProperties("oauth.kakao.resource")
    public ResourceServerProperties kakaoResourceProp() {
        return new ResourceServerProperties();
    }

    @Bean
    @Qualifier("googleResourceDetail")
    @ConfigurationProperties("oauth.google.client")
    public AuthorizationCodeResourceDetails googleResourceDetail() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @Qualifier("googleResourceProp")
    @ConfigurationProperties("oauth.google.resource")
    public ResourceServerProperties googleResourceProp() {
        return new ResourceServerProperties();
    }

    @Bean
    @Qualifier("facebookResourceDetail")
    @ConfigurationProperties("oauth.facebook.client")
    public AuthorizationCodeResourceDetails facebookResourceDetail() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @Qualifier("facebookResourceProp")
    @ConfigurationProperties("oauth.facebook.resource")
    public ResourceServerProperties facebookResourceProp() {
        return new ResourceServerProperties();
    }

    private Filter getOAuthFilter(String social, AuthorizationCodeResourceDetails details, ResourceServerProperties resourceProp) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(msgFormat(loginUrl, social));
        OAuth2RestTemplate template = new OAuth2RestTemplate(details, oAuth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceProp.getUserInfoUri(), details.getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        filter.setAuthenticationSuccessHandler((request, response, authentication) -> response.sendRedirect(msgFormat(loginCompleteUrl, social)));
        filter.setAuthenticationFailureHandler((request, response, exception) -> response.sendRedirect("/error"));
        return filter;
    }
}
