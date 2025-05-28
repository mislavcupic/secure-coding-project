package hr.algebra.semregprojectbackend.configuration;

import hr.algebra.semregprojectbackend.filter.JwtAuthFilter;
import hr.algebra.semregprojectbackend.service.UserDetailsServiceImpl;
import hr.algebra.semregprojectbackend.repository.UserRepository; // <-- DODAJ OVO!
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    private JwtAuthFilter jwtAuthFilter;
    private UserRepository userRepository; // <-- DODAJ OVO! Spring će ovo injektirati u SecurityConfiguration

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Dozvoli OPTIONS pre-flight zahtjeve za sve putanje
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Dozvoli pristup endpointima za autentifikaciju (login i refresh token) bez autorizacije
                        .requestMatchers("/auth/api/v1/login", "/auth/api/v1/refreshToken").permitAll()

                        // Zahtijevaj autorizaciju za sve putanje ispod /api/seminars
                        // Svi CRUD (GET, POST, PUT, DELETE) zahtjevi za seminare moraju biti autentificirani
                        .requestMatchers("/api/seminars/**").authenticated()

                        // Zahtijevaj autorizaciju za sve putanje ispod /api/students
                        // Svi CRUD (GET, POST, PUT, DELETE) zahtjevi za studente moraju biti autentificirani
                        .requestMatchers("/api/students/**").authenticated()

                        // Putanje koje počinju s /bugtracking/ također zahtijevaju autentifikaciju
                        .requestMatchers("/bugtracking/**").authenticated()

                        // Bilo koja druga putanja koja nije specifično navedena gore mora biti autentificirana
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(userRepository);
    }
}