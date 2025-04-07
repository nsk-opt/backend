package ru.nskopt.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.repositories.UserRepository;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
