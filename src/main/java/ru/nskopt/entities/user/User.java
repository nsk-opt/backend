package ru.nskopt.entities.user;

import jakarta.persistence.*;
import java.util.Collection;
import java.util.List;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Entity
@Table(name = "app_user")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String username;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }
}
