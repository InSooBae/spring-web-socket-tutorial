package com.socket.socketexample.global.config;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.Customizer.*;

/**
 * Web Security 설정
 */
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain web(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 기본값이 on인 csrf 취약점 보안을 해제한다. on으로 설정해도 되나 설정할경우 웹페이지에서 추가처리가 필요함.
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.
                )
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/chat/**").hasRole("USER") // chat으로 시작하는 리소스에 대한 접근 권한 설정
                        .anyRequest().permitAll()   // 나머지 리소스에 대한 접근 설정
                )
                .formLogin(withDefaults()); // 권한없이 페이지 접근하면 로그인 페이지로 이동한다.

        return http.build();
    }

    /**
     * 테스트를 위해 In-Memory에 계정을 임의로 생성한다.
     * 서비스에 사용시에는 DB데이터를 이용하도록 수정이 필요하다.
     */
    @Bean
    public UserDetailsService inMemoryUsers() {
        List<UserDetails> users = new ArrayList<>();
        users.add(
                User.builder()
                        .username("qospwmf")
                        .password("{noop}1234")
                        .roles("USER")
                        .build()
        );

        users.add(
                User.builder()
                        .username("abc")
                        .password("{noop}1234")
                        .roles("USER")
                        .build()
        );

        users.add(
                User.builder()
                        .username("guest")
                        .password("{noop}1234")
                        .roles("USER")
                        .build()
        );

        return new InMemoryUserDetailsManager(users);
    }
}
