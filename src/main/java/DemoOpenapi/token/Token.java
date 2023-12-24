package DemoOpenapi.token;

import DemoOpenapi.users.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Token {

    @Id
    @GeneratedValue
    private Integer id;

    private String token;

    private Boolean revoked;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
