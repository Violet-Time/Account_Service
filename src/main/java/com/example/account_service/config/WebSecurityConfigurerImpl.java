package com.example.account_service.config;

import com.example.account_service.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    public WebSecurityConfigurerImpl(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(getEncoder());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .mvcMatchers(HttpMethod.POST,"/api/auth/signup", "/actuator/shutdown").permitAll()
                    .mvcMatchers(HttpMethod.POST, "api/auth/changepass").hasAnyRole("USER", "ACCOUNTANT", "ADMINISTRATOR")
                    .mvcMatchers(HttpMethod.GET, "api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                    .mvcMatchers(HttpMethod.POST, "api/acct/payments").hasAnyRole("ACCOUNTANT")
                    .mvcMatchers(HttpMethod.PUT, "api/acct/payments").hasAnyRole("ACCOUNTANT")
                    .mvcMatchers( "api/admin/**").hasAnyRole("ADMINISTRATOR")
                    .mvcMatchers("api/security/events").hasRole("AUDITOR")
                    //.anyRequest().anonymous()
                    //.mvcMatchers(HttpMethod.DELETE, "api/admin/user/*").hasAnyRole("ADMINISTRATOR")
                    //.mvcMatchers(HttpMethod.PUT, "api/admin/user/role").hasAnyRole("ADMINISTRATOR")
                .and()
                    .csrf().disable()
                    .httpBasic()
                    .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .exceptionHandling()
                    .accessDeniedHandler(accessDeniedHandler());

    }

    @Bean
    public BasicAuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler();
    }
    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}
