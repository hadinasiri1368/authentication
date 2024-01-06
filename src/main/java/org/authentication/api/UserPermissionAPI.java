package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.UserPermissionDto;
import org.authentication.dto.UserRoleDto;
import org.authentication.model.*;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserPermissionAPI {
    @Autowired
    private GenericService<UserPermission> service;

    @PostMapping(path = "/api/userPermission/add")
    public Long addUserPermission(@RequestBody UserPermissionDto userPermissionDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserPermission userPermission = new UserPermission();
        userPermission.setId(userPermissionDto.getId());
        Permission permission = new Permission();
        permission.setId(userPermissionDto.getPermissionId());
        User user = new User();
        user.setId(userPermissionDto.getUserId());
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        service.insert(userPermission, userId);
        return userPermission.getId();
    }
    @PostMapping(path = "/api/userPermission/edit")
    public Long editUserPermission(@RequestBody UserPermissionDto userPermissionDto, HttpServletRequest request) throws Exception{
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserPermission userPermission = new UserPermission();
        userPermission.setId(userPermissionDto.getId());
        Permission permission = new Permission();
        permission.setId(userPermissionDto.getPermissionId());
        User user = new User();
        user.setId(userPermissionDto.getUserId());
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        service.update(userPermission ,userId);
        return userPermission.getId();
    }

    @PostMapping(path = "/api/userPermission/remove/{id}")
    public Long removeUserPermission(@PathVariable Long id) {
        service.delete(id, UserPermission.class);
        return id;
    }

    @GetMapping(path = "/api/userPermission/{id}")
    public UserPermission getUserPermission(@PathVariable Long id) {
        return service.findOne(UserPermission.class, id);
    }

    @GetMapping(path = "/api/userPermission")
    public List<UserPermission> listUserPermission() {
        return service.findAll(UserPermission.class);
    }
}
