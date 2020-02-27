package com.practice.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Order(1)
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private AuthenticationProvider authenticationProvider1;

    private AuthenticationProvider authenticationProvider2;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public UserDetailsService userDetailsService(){

        Set<UserDetails> users = new HashSet<>();
        users.add(new User("user_himanshu", "password",
                true, true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("USER"))));

        return new CustomUserDetailsService(users);
    }

    public SecurityConfig(@Qualifier("provider1") AuthenticationProvider authenticationProvider1,@Qualifier("provider2") AuthenticationProvider authenticationProvider2) {
        this.authenticationProvider1 = authenticationProvider1;
        this.authenticationProvider2 = authenticationProvider2;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        //@formatter:off

        auth
           .ldapAuthentication()
           .userDnPatterns("uid={0},ou=people")
           .groupSearchBase("ou=groups")
           .contextSource()
                .url("ldap://127.0.0.1:8389/dc=springframework,dc=org")
            .and()
            .passwordCompare()
              .passwordEncoder(new BCryptPasswordEncoder())
              .passwordAttribute("userPassword");

        //@formatter:on

        auth.authenticationProvider(authenticationProvider1);
        auth.authenticationProvider(authenticationProvider2);
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .headers()
                    .httpStrictTransportSecurity()
                        .includeSubDomains(true)
                        .preload(true)
                        .and()
                .and()
                    .requiresChannel()
                    .anyRequest().requiresSecure()
                .and()
                    .httpBasic()
                .and()
                   .authorizeRequests()
                   .mvcMatchers("/hello").authenticated();
    }
}
