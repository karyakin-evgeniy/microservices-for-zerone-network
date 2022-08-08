package org.proteam24.zeroneapplication.security;

import lombok.extern.slf4j.Slf4j;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.proteam24.zeroneapplication.security.jwt.JwtUser;
import org.proteam24.zeroneapplication.security.jwt.JwtUserFactory;
import org.proteam24.zeroneapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(@Lazy UserService userService){
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userService.findByEmail(email);
        if(userEntity == null){
            throw new UsernameNotFoundException("User with username: " + email + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(userEntity);
        log.info("IN loadUserByUsername - user with username: {} successfully created", email);
        return jwtUser;
    }

}
