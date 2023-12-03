package org.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserGroupRoleDto {
    private Long id;
    private Long f_user_group_id;
    private Long f_role_id;
}
