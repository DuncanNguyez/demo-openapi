package DemoOpenapi.configs;

import DemoOpenapi.Token.Token;
import DemoOpenapi.Token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
public class SecurityConfiguration {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private TokenRepository tokenRepository;

    private final LogoutSuccessHandler logoutSuccessHandler = (request, response, authentication) -> {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || authHeader.startsWith("Bearer ")) {
            return;
        }
        String jwt = authHeader.substring(7);
        Token token = tokenRepository.findByToken(jwt).orElse(null);
        if (token != null) {
            token.setRevoked(true);
            tokenRepository.save(token);
        }
        SecurityContextHolder.clearContext();
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.anyRequest().permitAll()
                )
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(l -> l.logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .build();
    }
}
