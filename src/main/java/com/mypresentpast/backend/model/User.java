package com.mypresentpast.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad representante de user_account.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_account")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user_account")
    private Long id;

    // Alias visible del usuario
    @Column(name = "profile_username", nullable = false, unique = true, length = 50)
    private String profileUsername;

    @Column(name = "password",nullable = false, length = 100)
    private String password;

    // Email usado para el login
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    // Solo para usuario con rol NORMAL
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.NORMAL;

    @Column(name = "avatar")
    private String avatar;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Post> posts;

    // Implementado por UserDetails
    @Override
    public String getUsername() {
        return this.email;
    }

    // Implementado por UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


    // Métodos innecesarios debido al jwt, pero obligatorios por UserDetails
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
