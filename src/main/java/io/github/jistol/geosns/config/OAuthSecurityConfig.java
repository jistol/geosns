package io.github.jistol.geosns.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired @Qualifier("kakaoOAuthFilter") private Filter kakaoOAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        http
            .httpBasic().disable()
            .csrf().disable()
            //.addFilterBefore(filter, CsrfFilter.class)
            .addFilterBefore(oauth2Filter(), BasicAuthenticationFilter.class)
            .authorizeRequests()
                .antMatchers("/demo/**","/google/**", "/", "/map", "/login/**",  "/dist/**", "/img/**", "/admin/**").permitAll()
                .antMatchers("/browser/**", "/users", "/users/**", "/posts", "/posts/**", "/profile", "/profile/**").permitAll()
                .anyRequest().authenticated()
            .and()
                .headers().frameOptions().disable()
//            .and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint())
                //.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
            .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("SESSION")
                .invalidateHttpSession(true);
    }

    private Filter oauth2Filter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(kakaoOAuthFilter);
        filter.setFilters(filters);
        return filter;
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            log.error("security error : {}", authException.getMessage());

            AbstractAuthenticationToken auth = (AbstractAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.debug("error -> isAuthenticated : {}, grant : {}", auth.isAuthenticated(), auth.getAuthorities());
                if (auth != null && auth instanceof OAuth2Authentication) {
                    OAuth2Authentication authentication = (OAuth2Authentication)auth;
                    Map<String, Object> map = (Map<String, Object>) authentication.getUserAuthentication().getDetails();

                    log.debug("auth : {}", authentication.getAuthorities());

                    map.forEach((key, value) -> {
                        log.debug("key[{}]:{}", key, value);
                    });
                }
            }

            response.sendError( HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized" );
        };
    }
}
