package org.authentication.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGroupDetailDto {
    private Long id;
    private Long userGroupId;
    private Long userId;
}
