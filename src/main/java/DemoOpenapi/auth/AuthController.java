package DemoOpenapi.auth;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
@Autowired
AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/refresh-token")
    private ResponseEntity<AuthResponse> refreshToken (HttpServletRequest request, HttpServletResponse response) throws Exception {
        return ResponseEntity.ok(authService.refreshToken(request,response));
    }
}
