package com.practice.security.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Log4j2
@Component("provider2")
public class CustomAuthenticationProvider2 implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        log.info("Custom Authentication 2");

        if (authentication.getName().equalsIgnoreCase("poonam")) {
            return new UsernamePasswordAuthenticationToken(authentication.getName(), authentication.getCredentials(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
        }

        throw new BadCredentialsException("User not Authenticated");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
