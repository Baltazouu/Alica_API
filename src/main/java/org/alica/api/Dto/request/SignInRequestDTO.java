package org.alica.api.Dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignInRequestDTO(

        @Email String email,
        @NotNull String password
) { }
