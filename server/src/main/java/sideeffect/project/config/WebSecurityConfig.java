package sideeffect.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sideeffect.project.security.LoginFailureHandler;
import sideeffect.project.security.LoginSuccessHandler;
import sideeffect.project.security.UserDetailsServiceImpl;
import sideeffect.project.security.JwtExceptionHandlerFilter;
import sideeffect.project.security.JwtFilter;
import sideeffect.project.security.JwtTokenProvider;
import sideeffect.project.security.oauth.Oauth2AuthenticationSuccessHandler;
import sideeffect.project.security.oauth.Oauth2Service;
import sideeffect.project.security.oauth.OauthExceptionHandler;

@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig{

    @Value("${jwt.secret}")
    private String secretKey;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final Oauth2Service oauth2Service;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .authorizeRequests()
                .antMatchers("/api/user/join", "/api/user/mypage/**").permitAll()
                .antMatchers(HttpMethod.POST, "/**").authenticated()
                .antMatchers(HttpMethod.GET, "/api/free-boards/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/like/**").hasAnyRole("USER", "ADMIN")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new OauthExceptionHandler(), OAuth2AuthorizationRequestRedirectFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(), JwtFilter.class)
                .formLogin()
                .loginProcessingUrl("/api/user/login")
                .usernameParameter("email")
                .successHandler(new LoginSuccessHandler(jwtTokenProvider))
                .failureHandler(new LoginFailureHandler())
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(oauth2Service).and()
                .successHandler(new Oauth2AuthenticationSuccessHandler(jwtTokenProvider))
                .and()
                .build();
    }
}
