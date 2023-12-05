package org.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGroupRoleDto {
    private Long id;
    private Long userGroupId;
    private Long roleId;
}
