package org.authentication.dto.ResponseDto;

import lombok.*;
import org.authentication.model.User;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserPersonDto {
    private User user;
    private Person person;
}