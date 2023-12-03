package org.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RolePermissionDto {
    private Long id;
    private Long f_role_id;
    private Long f_permission_id;
}

