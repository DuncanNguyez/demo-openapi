package DemoOpenapi.auth;

import DemoOpenapi.users.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName ;
    private Role role;
}
