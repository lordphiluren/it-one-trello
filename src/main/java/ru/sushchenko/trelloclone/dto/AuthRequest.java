package ru.sushchenko.trelloclone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.sushchenko.trelloclone.utils.validation.LoginRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AuthRequest {
    @NotNull(message = "Email can not be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Username can not be empty", groups = {LoginRequest.class})
    @Size(min = 4, max = 32, message = "Username size should be between 4 and 32")
    private String username;
    @Size(min = 6, max = 20, message = "Password size should be between 6 and 20")
    @NotNull(message = "Password can not be empty", groups = {LoginRequest.class})
    private String password;
}
