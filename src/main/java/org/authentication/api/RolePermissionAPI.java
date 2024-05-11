package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.RolePermissionDto;
import org.authentication.model.Permission;
import org.authentication.model.Role;
import org.authentication.model.RolePermission;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class RolePermissionAPI {
    @Autowired
    private GenericService<RolePermission> service;

    @PostMapping(path = "/api/rolePermission/add")
    public Long addRolePermission(@RequestBody RolePermissionDto rolePermissionDto, HttpServletRequest request) throws Exception{
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        RolePermission rolePermission = new RolePermission();
        Role role = new Role();
        role.setId(rolePermissionDto.getRoleId());
        Permission permission = new Permission();
        permission.setId(rolePermissionDto.getPermissionId());
        rolePermission.setPermission(permission);
        rolePermission.setRole(role);
        service.insert(rolePermission, userId);
        return rolePermission.getId();
    }

    @PutMapping(path = "/api/rolePermission/edit")
    public Long editRolePermission(@RequestBody RolePermissionDto rolePermissionDto, HttpServletRequest request) throws Exception{
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(rolePermissionDto.getId());
        Role role = new Role();
        role.setId(rolePermissionDto.getPermissionId());
        Permission permission = new Permission();
        permission.setId(rolePermissionDto.getPermissionId());
        rolePermission.setPermission(permission);
        rolePermission.setRole(role);
        service.update(rolePermission, userId, RolePermission.class);
        return rolePermission.getId();
    }

    @DeleteMapping(path = "/api/rolePermission/remove/{id}")
    public Long removeRolePermission(@PathVariable Long id) {
        service.delete(id, RolePermission.class);
        return id;
    }

    @GetMapping(path = "/api/rolePermission/{id}")
    public RolePermission getRolePermission(@PathVariable Long id) {
        return service.findOne(RolePermission.class, id);
    }

    @GetMapping(path = "/api/rolePermission")
    public Page<RolePermission> listRolePermission(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(RolePermission.class,page,size);
    }
}
