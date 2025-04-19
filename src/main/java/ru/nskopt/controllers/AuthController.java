package ru.nskopt.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.nskopt.dto.user.security.UserAuthRequest;
import ru.nskopt.dto.user.security.UserAuthResponse;
import ru.nskopt.dto.user.security.UserRegistrationRequest;
import ru.nskopt.services.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final UserService authService;

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/register")
  public void register(@Valid @RequestBody UserRegistrationRequest request) {
    authService.register(request);
  }

  @PostMapping("/login")
  public UserAuthResponse login(@Valid @RequestBody UserAuthRequest request) {
    return authService.authenticate(request);
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/check-admin")
  @PreAuthorize("hasAnyRole('ADMIN')")
  public void checkAdmin() {
    log.info("Admin role validated.");
  }

  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/check-manager")
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public void checkManager() {
    log.info("Manager role validated.");
  }
}
