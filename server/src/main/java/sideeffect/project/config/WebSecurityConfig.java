package sideeffect.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sideeffect.project.security.*;
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig{

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;


    @Bean
    public LoginSuccessHandler loginSuccessHandler(RefreshTokenProvider refreshTokenProvider) {
        return new LoginSuccessHandler(refreshTokenProvider);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/user/join", "/api/user/mypage/**", "/api/user/duple/**", "/api/social/login").permitAll()
            .antMatchers(HttpMethod.POST, "/api/token/at-issue/**").permitAll()
            .antMatchers(HttpMethod.POST, "/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/free-boards/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/like/**").hasAnyRole("USER", "ADMIN")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new SecurityExceptionHandlerFilter(), JwtFilter.class)
//                .formLogin()
//                    .loginProcessingUrl("/api/user/login")
//                    .usernameParameter("email")
//                    .successHandler(loginSuccessHandler(refreshTokenProvider))
//                    .failureHandler(loginFailureHandler())
//                    .and()
                .build();
    }
}
