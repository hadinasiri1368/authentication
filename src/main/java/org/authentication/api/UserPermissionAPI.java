package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.UserPermissionDto;
import org.authentication.model.*;
import org.authentication.service.GenericService;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserPermissionAPI {
    @Autowired
    private GenericService<UserPermission> service;
    @Autowired
    private UserService userService;


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

    @PutMapping (path = "/api/userPermission/edit")
    public Long editUserPermission(@RequestBody UserPermissionDto userPermissionDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserPermission userPermission = new UserPermission();
        userPermission.setId(userPermissionDto.getId());
        Permission permission = new Permission();
        permission.setId(userPermissionDto.getPermissionId());
        User user = new User();
        user.setId(userPermissionDto.getUserId());
        userPermission.setUser(user);
        userPermission.setPermission(permission);
        service.update(userPermission, userId, UserPermission.class);
        return userPermission.getId();
    }

    @DeleteMapping(path = "/api/userPermission/remove/{id}")
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

    @GetMapping(path = "/api/userPermissionPerUser/{userId}")
    public List<UserPermission> userPermissions(@PathVariable Long userId) {
        return userService.findUserPermission(userId);
    }
}
