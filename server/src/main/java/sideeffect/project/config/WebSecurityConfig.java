package sideeffect.project.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sideeffect.project.security.*;

@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig{

    @Value("${jwt.secret}")
    private String secretKey;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final UserDetailsServiceImpl userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
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
                    //.antMatchers(HttpMethod.POST, "/api/oauth/**").permitAll()
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                .addFilterAt(loginFilter(http), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), JwtFilter.class)
                .build();
    }


    @Bean
    public LoginFilter loginFilter(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationManager(authenticationManager(http));
        loginFilter.setUsernameParameter("email");
        loginFilter.setFilterProcessesUrl("/api/user/login");
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        return loginFilter;
    }

    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtTokenProvider);
    }

    /*@Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(bCryptPasswordEncoder, userDetailsService);
    }*/

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider());
        return authenticationManagerBuilder.build();
    }
    /*@Bean
    public AuthenticationManager authenticationManager() throws Exception {

        return this.authenticationConfiguration.getAuthenticationManager();
    }*/

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(bCryptPasswordEncoder, userDetailsService);
    }
}
