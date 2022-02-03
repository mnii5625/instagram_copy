package com.example.instagram.Config;

import com.example.instagram.Handler.AuthFailureHandler;
import com.example.instagram.Handler.AuthSuccessHandler;
import com.example.instagram.Jwt.JwtAuthenticationFilter;
import com.example.instagram.Jwt.JwtTokenProvider;
import com.example.instagram.Service.LoginService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Setter
@RequiredArgsConstructor

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginService loginService;
    private final AuthFailureHandler authFailureHandler;
    private final AuthSuccessHandler authSuccessHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.csrf().disable();
        http.httpBasic().disable();
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers("/","/signup/**","/css/**","/js/**","/images/**","/test/**","/api/test","/static/images/**").permitAll()
                .antMatchers("/main").authenticated()
                .anyRequest().authenticated()
                .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate),
                    UsernamePasswordAuthenticationFilter.class);
        http.formLogin().disable();

                /*
            .formLogin()
                .loginPage("/")
                .usernameParameter("id")
                .passwordParameter("password")
                .permitAll()
                .defaultSuccessUrl("/test")
                .failureHandler(authFailureHandler)
                .successHandler(authSuccessHandler);
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("remember-me", "JSESSIONID");*/
    }
    /*@Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(loginService);
    }*/
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}
}
