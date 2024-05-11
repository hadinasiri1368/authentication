package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.UserGroupRoleDto;
import org.authentication.model.*;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupRoleAPI {
    @Autowired
    private GenericService<UserGroupRole> service;

    @PostMapping(path = "/api/userGroupRole/add")
    public Long addUserGroupRole(@RequestBody UserGroupRoleDto userGroupRoleDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserGroupRole userGroupRole = new UserGroupRole();
        userGroupRole.setId(userGroupRoleDto.getId());
        Role role = new Role();
        role.setId(userGroupRoleDto.getRoleId());
        UserGroup userGroup = new UserGroup();
        userGroup.setId(userGroupRoleDto.getUserGroupId());
        userGroupRole.setUserGroup(userGroup);
        userGroupRole.setRole(role);
        service.insert(userGroupRole, userId);
        return userGroupRole.getId();
    }

    @PutMapping(path = "/api/userGroupRole/edit")
    public Long editUserGroupRole(@RequestBody UserGroupRoleDto userGroupRoleDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserGroupRole userGroupRole = new UserGroupRole();
        userGroupRole.setId(userGroupRoleDto.getId());
        Role role = new Role();
        role.setId(userGroupRoleDto.getRoleId());
        UserGroup userGroup = new UserGroup();
        userGroup.setId(userGroupRoleDto.getUserGroupId());
        userGroupRole.setUserGroup(userGroup);
        userGroupRole.setRole(role);
        service.update(userGroupRole, userId, UserGroupRole.class);
        return userGroupRole.getId();
    }

    @DeleteMapping(path = "/api/userGroupRole/remove/{id}")
    public Long removeUserGroupRole(@PathVariable Long id) {
        service.delete(id, UserGroupRole.class);
        return id;
    }

    @GetMapping(path = "/api/userGroupRole/{id}")
    public UserGroupRole getUserGroupRole(@PathVariable Long id) {
        return service.findOne(UserGroupRole.class, id);
    }

    @GetMapping(path = "/api/userGroupRole")
    public Page<UserGroupRole> listUserGroupRole(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(UserGroupRole.class, page, size);
    }
}
