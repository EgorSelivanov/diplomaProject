package ru.selivanov.springproject.diplomaProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.selivanov.springproject.diplomaProject.services.UserDetailsService;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    //настраиваем аутентификацию
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(getPasswordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login", "/auth/registration", "/error").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/student/**").hasRole("STUDENT")
                        .requestMatchers("/teacher/**", "/grade", "/assignment", "/attendance").hasRole("TEACHER")
                        .requestMatchers("/admin", "/grade", "/assignment", "/attendance").hasRole("ADMIN")
                        .anyRequest().hasAnyRole("STUDENT", "TEACHER", "ADMIN"))
                .formLogin()
                    .loginPage("/auth/login")
                    .loginProcessingUrl("/process_login")
                    .defaultSuccessUrl("/auth/redirect", true)
                    .failureUrl("/auth/login?error")
                .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/auth/login")
                .and()
                .rememberMe()
                    .tokenValiditySeconds(86400) // 24 часа
                .and()
                .sessionManagement()
                    .sessionFixation().migrateSession()
                    .maximumSessions(1)
                    .expiredUrl("/login")
                    .maxSessionsPreventsLogin(false)
                    .sessionRegistry(sessionRegistry());;
        return http.build();
    }
}
