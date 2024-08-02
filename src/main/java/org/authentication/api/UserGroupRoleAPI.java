package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.UserGroupRoleDto;
import org.authentication.model.*;
import org.authentication.service.UserGroupRoleService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupRoleAPI {
    private final UserGroupRoleService userGroupRoleService;

    public UserGroupRoleAPI( UserGroupRoleService userGroupRoleService) {

        this.userGroupRoleService = userGroupRoleService;
    }

    @PostMapping(path = "/authentication/userGroupRole/add")
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
        userGroupRoleService.insert(userGroupRole, userId);
        return userGroupRole.getId();
    }

    @PutMapping(path = "/authentication/userGroupRole/edit")
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
        userGroupRoleService.update(userGroupRole, userId);
        return userGroupRole.getId();
    }

    @DeleteMapping(path = "/authentication/userGroupRole/remove/{id}")
    public Long removeUserGroupRole(@PathVariable Long id) {
        userGroupRoleService.delete(id);
        return id;
    }

    @GetMapping(path = "/authentication/userGroupRole/{id}")
    public UserGroupRole getUserGroupRole(@PathVariable Long id) {
        return userGroupRoleService.findOne(UserGroupRole.class, id);
    }

    @GetMapping(path = "/authentication/userGroupRole")
    public Page<UserGroupRole> listUserGroupRole(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return userGroupRoleService.findAll(UserGroupRole.class, page, size);
    }

    @GetMapping(path = "/authentication/rolesUserGroup/{userGroupId}")
    public List<UserGroupRole> findRolesUserGroup(@PathVariable Long userGroupId) {
        return userGroupRoleService.findRolesUserGroup(userGroupId);
    }

}
