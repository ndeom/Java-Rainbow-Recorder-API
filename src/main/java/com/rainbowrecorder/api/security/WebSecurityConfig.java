package com.rainbowrecorder.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public JWTTokenFilter jwtTokenFilter() {
        return new JWTTokenFilter();
    }

    // HttpSecurity allows web based security for specific http requests. By default it is applied
    // to all requests.
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Disables Cross-Site Resource Forgery support
        http.csrf()
                .disable()

                // Adds cors (Cross-Origin Resource Sharing) support. Because the corsConfigurationSource Bean
                // was defined below, that CorsConfiguration is used for the application
                .cors()
                .and()

                // Allows configuring of exception handling for the application. HttpStatusEntryPoint is an
                // AuthenticationEntryPoint that is used to set the default response if no match is found
                // for the request. In this case, the default response is an unauthorized status (403).
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()

                // This sets the session management type to stateless, because the application is using JSON web tokens (JWTs).
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // Configures the requests that will be accepted by the application and their associated permissions
                // (whether users will need to be authenticated to reach certain routes).
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers(HttpMethod.GET, "/api/posts", "api/posts/singlepost").authenticated()
                .antMatchers(HttpMethod.POST, "api/posts").authenticated()
                .antMatchers(HttpMethod.PUT, "/api/posts/like", "/api/posts/unlike", "/api/posts/comment", "/api/posts/uncomment").authenticated()
                .antMatchers(HttpMethod.GET, "/api/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users/login", "/api/users/register", "/api/users/refresh").permitAll()
                .anyRequest().authenticated();

        // Adds JWT filter before all requests
        http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    // Bean to configure the CORS settings for the application
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Accept", "Cache-Control", "Content-Type", "Access-Control-Allow-Headers"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
