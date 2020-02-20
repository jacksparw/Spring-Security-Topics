package com.practice.security.config;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private Map<String, UserDetails> users;

    public CustomUserDetailsService(Set<UserDetails> users) {

        this.users = users.stream()
                .collect(Collectors.toMap(entry -> entry.getUsername(), entry -> entry));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (users.containsKey(username)) {

            UserDetails userDetails = users.get(username);

            return new User(userDetails.getUsername()
                    , userDetails.getPassword()
                    , userDetails.isEnabled()
                    , userDetails.isAccountNonExpired()
                    , userDetails.isCredentialsNonExpired()
                    , userDetails.isAccountNonLocked()
                    , userDetails.getAuthorities());
        }


        throw new UsernameNotFoundException("user id not found");
    }
}
