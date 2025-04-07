package ru.nskopt.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nskopt.dto.user.security.UserAuthRequest;
import ru.nskopt.dto.user.security.UserAuthResponse;
import ru.nskopt.dto.user.security.UserRegistrationRequest;
import ru.nskopt.entities.user.Role;
import ru.nskopt.entities.user.User;
import ru.nskopt.exceptions.AuthenticationFailedException;
import ru.nskopt.exceptions.UserExistsException;
import ru.nskopt.repositories.UserRepository;
import ru.nskopt.utils.JwtUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;

  public void register(UserRegistrationRequest request) {
    log.info("Starting registration for user: {}", request.getUsername());

    if (userRepository.existsByUsername(request.getUsername())) {
      log.info("User already exists: {}", request.getUsername());
      throw new UserExistsException("User already exists: " + request.getUsername());
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.ROLE_USER);

    userRepository.save(user);
    log.info("User registered successfully: {}", request.getUsername());
  }

  public UserAuthResponse authenticate(UserAuthRequest request) {
    log.info("Starting authentication for user: {}", request.getUsername());

    try {
      Authentication auth =
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

      Authentication authenticatedAuth = authenticationManager.authenticate(auth);
      SecurityContextHolder.getContext().setAuthentication(authenticatedAuth);

      var userDetails = (UserDetails) authenticatedAuth.getPrincipal();
      log.info("User authenticated successfully: {}", request.getUsername());

      return new UserAuthResponse(jwtUtils.generateToken(userDetails));
    } catch (AuthenticationException e) {
      log.info("Authentication failed for user: {}", request.getUsername());
      throw new AuthenticationFailedException("Invalid email or password");
    }
  }
}
