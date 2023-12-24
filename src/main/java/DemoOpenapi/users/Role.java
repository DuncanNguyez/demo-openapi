package DemoOpenapi.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(Set.of(
            Permission.USER_READ
    )),
    MANAGER(Set.of(
            Permission.USER_READ,
            Permission.MANAGER_READ,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
    )),
    ADMIN(Set.of(
            Permission.USER_READ,
            Permission.ADMIN_READ,
            Permission.ADMIN_CREATE,
            Permission.ADMIN_UPDATE,
            Permission.ADMIN_DELETE,
            Permission.MANAGER_READ,
            Permission.MANAGER_CREATE,
            Permission.MANAGER_UPDATE,
            Permission.MANAGER_DELETE
    ));
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .toList();
    }
}
