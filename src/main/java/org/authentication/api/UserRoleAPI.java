package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.UserRoleDto;
import org.authentication.model.*;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserRoleAPI {
    @Autowired
    private GenericService<UserRole> service;

    @PostMapping(path = "/api/userRole/add")
    public Long addUserRole(@RequestBody UserRoleDto userRoleDto, HttpServletRequest request) {
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
    @PostMapping(path = "/api/userRole/edit")
    public Long editUserRole(@RequestBody UserRole userRole, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(userRole ,userId);
        return userRole.getId();
    }

    @PostMapping(path = "/api/userRole/remove/{id}")
    public Long removeUserRole(@PathVariable Long id) {
        service.delete(new UserRole(id, null, null));
        return id;
    }

    @GetMapping(path = "/api/userRole/{id}")
    public UserRole getUserRole(@PathVariable Long id) {
        return service.findOne(UserRole.class, id);
    }

    @GetMapping(path = "/api/userRole")
    public List<UserRole> listUserRole() {
        return service.findAll(UserRole.class);
    }
}
