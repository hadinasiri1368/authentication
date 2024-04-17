package org.authentication.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
}
