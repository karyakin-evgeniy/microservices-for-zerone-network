//package org.proteam24.zeroneapplication.security.jwt;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//
//public class JwtConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//    private JwtTokenProvider jwtTokenProvider;
//    private final HandlerExceptionResolver handlerExceptionResolver;
//    private final AuthenticationManager authenticationManager;
//
//    public JwtConfigurer(JwtTokenProvider jwtTokenProvider, HandlerExceptionResolver handlerExceptionResolver, AuthenticationManager authenticationManager) {
//                this.jwtTokenProvider = jwtTokenProvider;
//        this.handlerExceptionResolver = handlerExceptionResolver;
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Override
//    public void configure(HttpSecurity httpSecurity) throws Exception {
//        JwtTokenFilter jwtTokenFilter = new JwtTokenFilter(authenticationManager, jwtTokenProvider, handlerExceptionResolver);
//        httpSecurity.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}