package com.mycompany.app.recipe.config;

import com.mycompany.app.recipe.util.StringConstants;

import org.springframework.context.annotation.Bean;
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
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        PasswordEncoder passwordEncoder = passwordEncoder();
        builder.inMemoryAuthentication()
            .withUser(StringConstants.ADMIN)
            .password(passwordEncoder.encode(StringConstants.ADMIN))
            .roles(StringConstants.ADMIN, StringConstants.USER);

        builder.inMemoryAuthentication()
            .withUser(StringConstants.USER)
            .password(passwordEncoder.encode(StringConstants.USER))
            .roles(StringConstants.USER);
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
                .hasRole(StringConstants.ADMIN)
            .antMatchers(HttpMethod.DELETE, "/demo-api/recipeManagement/v1/recipe/**")
                .hasRole(StringConstants.ADMIN)
            .antMatchers("/**")
                .fullyAuthenticated()
        .and()
            .httpBasic();
    }
}
