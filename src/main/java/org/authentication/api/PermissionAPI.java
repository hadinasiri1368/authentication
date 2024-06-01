package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.authentication.model.Permission;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class PermissionAPI {
    @Autowired
    private GenericService<Permission> service;

    @PostMapping(path = "/authentication/permission/add")
    public Long addPermission(@RequestBody Permission permission, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(permission, userId);
        return permission.getId();
    }

    @PutMapping(path = "/authentication/permission/edit")
    public Long editPermission(@RequestBody Permission permission, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(permission, userId, Permission.class);
        return permission.getId();
    }

    @DeleteMapping(path = "/authentication/permission/remove/{id}")
    public Long removePermission(@PathVariable Long id) {
        service.delete(id, Permission.class);
        return id;
    }

    @GetMapping(path = "/authentication/permission/{id}")
    public Permission getPermission(@PathVariable Long id) {
        return service.findOne(Permission.class, id);
    }

    @GetMapping(path = "/authentication/permission")
    public Page<Permission> listPermission(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(Permission.class, page, size);
    }
}
