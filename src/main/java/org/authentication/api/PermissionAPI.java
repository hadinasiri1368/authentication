package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.authentication.model.Permission;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class PermissionAPI {
    @Autowired
    private GenericService<Permission> service;

    @PostMapping(path = "/api/permission/add")
    public Long addPermission(@RequestBody Permission permission, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(permission, userId);
        return permission.getId();
    }

    @PostMapping(path = "/api/permission/edit")
    public Long editPermission(@RequestBody Permission permission, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(permission, userId, Permission.class);
        return permission.getId();
    }

    @PostMapping(path = "/api/permission/remove/{id}")
    public Long removePermission(@PathVariable Long id) {
        service.delete(id, Permission.class);
        return id;
    }

    @GetMapping(path = "/api/permission/{id}")
    public Permission getPermission(@PathVariable Long id) {
        return service.findOne(Permission.class, id);
    }

    @GetMapping(path = "/api/permission")
    public List<Permission> listPermission() {
        return service.findAll(Permission.class);
    }
}
