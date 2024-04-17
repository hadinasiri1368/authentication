package org.authentication.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RolePermissionDto {
    private Long id;
    private Long roleId;
    private Long permissionId;
}

