package org.proteam24.zeroneapplication.config;


import org.proteam24.zeroneapplication.security.jwt.JwtConfigurer;
import org.proteam24.zeroneapplication.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final JwtTokenProvider jwtTokenProvider;

    private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/**";
    private static final String ACCOUNT_ENDPOINT = "/api/v1/account/**";
    private static final String AUTHORIZED_ENDPOINT = "/api/v1/users/**";
    private static final String POST_ENDPOINT = "/api/v1/post/**";
    private static final String SWAGGER_ENDPOINT = "/swagger-ui/**";
    private static final String API_DOCS = "/v3/api-docs/**";
    private static final String ACTUATOR = "/actuator**";
    private static final String ACTUATOR2 = "/actuator/**";
    private static final String SOURCE = "/api/live/ws";

    @Autowired
    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT, ACCOUNT_ENDPOINT, AUTHORIZED_ENDPOINT).permitAll()
                .antMatchers(API_DOCS, SWAGGER_ENDPOINT).permitAll()
                .antMatchers(ACTUATOR, ACTUATOR2, SOURCE).permitAll()
                .antMatchers(POST_ENDPOINT).authenticated()
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }
}