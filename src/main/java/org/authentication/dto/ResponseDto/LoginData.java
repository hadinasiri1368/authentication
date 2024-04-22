package org.authentication.dto.ResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginData {
    private String username;
    private String name;
    private String family;
    private boolean isActive;
    private boolean isAdmin;
    private String token;
}
