package com.mycompany.app.recipe.config;


import com.mycompany.app.recipe.util.StringConstants;

import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    
    private Environment environment;

    public SecurityConfiguration(Environment environment) {
        this.environment = environment;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();
        builder.inMemoryAuthentication()
            .withUser(environment.getProperty("userconfig.user1.username"))
            .password(passwordEncoder.encode(environment.getProperty("userconfig.user1.password")))
            .roles(environment.getProperty("userconfig.user1.roles").split(","));

        builder.inMemoryAuthentication()
            .withUser(environment.getProperty("userconfig.user2.username"))
            .password(passwordEncoder.encode(environment.getProperty("userconfig.user2.password")))
            .roles(environment.getProperty("userconfig.user2.roles").split(","));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .csrf()
            .disable()
            .exceptionHandling()
        .and()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/demo-api/recipeManagement/v1/recipe")
                .hasAuthority(StringConstants.ADMIN)
            .antMatchers(HttpMethod.DELETE, "/demo-api/recipeManagement/v1/recipe")
                .hasAuthority(StringConstants.ADMIN)
            .antMatchers(HttpMethod.GET, "/demo-api/recipeManagement/v1/recipe")
                .fullyAuthenticated()
            .antMatchers(HttpMethod.PUT, "/demo-api/recipeManagement/v1/recipe")
                .fullyAuthenticated()
            .antMatchers(HttpMethod.PATCH, "/demo-api/recipeManagement/v1/recipe")
                .fullyAuthenticated()
        .and()
            .httpBasic();
    }
}
