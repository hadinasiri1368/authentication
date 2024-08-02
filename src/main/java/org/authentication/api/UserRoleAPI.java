package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.UserRoleDto;
import org.authentication.model.*;
import org.authentication.service.GenericService;
import org.authentication.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserRoleAPI {
    private final GenericService<UserRole> service;
    private final UserService userService;

    public UserRoleAPI(GenericService<UserRole> service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping(path = "/authentication/userRole/add")
    public Long addUserRole(@RequestBody UserRoleDto userRoleDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserRole userRole = new UserRole();
        userRole.setId(userRoleDto.getId());
        Role role = new Role();
        role.setId(userRoleDto.getRoleId());
        User user = new User();
        user.setId(userRoleDto.getUserId());
        userRole.setUser(user);
        userRole.setRole(role);
        service.insert(userRole, userId);
        return userRole.getId();
    }

    @PutMapping(path = "/authentication/userRole/edit")
    public Long editUserRole(@RequestBody UserRoleDto userRoleDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserRole userRole = new UserRole();
        userRole.setId(userRoleDto.getId());
        Role role = new Role();
        role.setId(userRoleDto.getRoleId());
        User user = new User();
        user.setId(userRoleDto.getUserId());
        userRole.setUser(user);
        userRole.setRole(role);
        service.update(userRole, userId, UserRole.class);
        return userRole.getId();
    }

    @DeleteMapping(path = "/authentication/userRole/remove/{id}")
    public Long removeUserRole(@PathVariable Long id) {
        service.delete(id, UserRole.class);
        return id;
    }

    @GetMapping(path = "/authentication/userRole/{id}")
    public UserRole getUserRole(@PathVariable Long id) {
        return service.findOne(UserRole.class, id);
    }

    @GetMapping(path = "/authentication/userRole")
    public Page<UserRole> listUserRole(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(UserRole.class, page, size);
    }

    @GetMapping(path = "/authentication/userRolePerUser/{userId}")
    public List<UserRole> userRoles(@PathVariable Long userId) {
        return userService.findUserRole(userId);
    }

}
