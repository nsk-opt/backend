package ru.nskopt.dto.user.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthRequest {
  private String username;
  private String password;
}
