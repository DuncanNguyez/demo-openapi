package DemoOpenapi.configs;

import DemoOpenapi.token.Token;
import DemoOpenapi.token.TokenRepository;
import DemoOpenapi.users.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    private final String[] WHITE_LIST = {
            "/api/v1/auth/**",
            "/api-docs",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui/index.html",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/books.json").hasAnyAuthority(Permission.USER_READ.name())
                        .requestMatchers(HttpMethod.POST, "/books.json").hasAnyAuthority(Permission.ADMIN_CREATE.name())
                        .requestMatchers(HttpMethod.PUT, "/books.json").hasAnyAuthority(Permission.ADMIN_UPDATE.name())
                        .requestMatchers(HttpMethod.DELETE, "/books.json").hasAnyAuthority(Permission.ADMIN_DELETE.name())
                        .anyRequest().authenticated()
                )
                .sessionManagement(ss -> ss.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(l -> l.logoutUrl("/api/v1/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .build();
    }
}
