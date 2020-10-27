package com.santander.seclog.security;

import com.santander.seclog.security.jwt.AuthEntryPointJwt;
import com.santander.seclog.security.jwt.AuthTokenFilter;
import com.santander.seclog.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO: Para probar solo backend, descomentar lo de abajo, si es para probar integracion con front: comentarlo

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}user").roles("USER").and()
                .withUser("admin").password("{noop}admin").roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        final String URL = "api/meetings/";

       http.httpBasic().and().authorizeRequests()
                .antMatchers(HttpMethod.POST, URL + "rooms").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, URL + "roomReserved").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, URL + "rooms/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, URL + "rooms").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, URL + "reserve/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, URL + "inscript").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, URL + "/beers/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, URL + "/weather/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/rooms/**").hasRole("ADMIN")
                .anyRequest().authenticated().and().csrf().disable();
    }

    // Descomentar hasta este punto

    //TODO: Para usar integracion con frontend: beer-project descomentar todo lo de abajo

    /*
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

   @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .antMatchers("/api/meetings/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }*/
}
