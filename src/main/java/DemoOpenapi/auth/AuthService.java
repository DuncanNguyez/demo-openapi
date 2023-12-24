package DemoOpenapi.auth;

import DemoOpenapi.Token.Token;
import DemoOpenapi.Token.TokenRepository;
import DemoOpenapi.jwts.JwtService;
import DemoOpenapi.users.User;
import DemoOpenapi.users.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;
    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .build();
        User userSaved = userRepository.save(user);
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveToken(user, accessToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public AuthResponse authenticate(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveToken(user, accessToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }


    public AuthResponse refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new Exception("refresh-token not found!");
        }
        String refreshToken = authHeader.substring(7);
        String email = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        if (jwtService.isTokenValid(refreshToken)) {
            String accessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);
            saveToken(user, accessToken);
            return AuthResponse.builder()
                    .refreshToken(refreshToken)
                    .accessToken(newRefreshToken)
                    .build();
        }

        throw new Exception("refresh-token valid");
    }

    private void saveToken(User user, String accessToken) {
        Token token = Token.builder()
                .token(accessToken)
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }
}
