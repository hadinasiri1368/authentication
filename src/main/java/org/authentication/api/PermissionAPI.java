package org.authentication.api;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.authentication.model.Permission;
import java.util.List;

@RestController
public class PermissionAPI {
    @Autowired
    private GenericService<Permission> service;

    @PostMapping(path = "/api/permission/add")
    public Long addPermission(@RequestBody Permission permission, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(permission, userId);
        return permission.getId();
    }

    @PostMapping(path = "/api/permission/edit")
    public Long editPermission(@RequestBody Permission permission, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(permission, userId);
        return permission.getId();
    }

    @PostMapping(path = "/api/permission/remove/{id}")
    public Long removePermission(@PathVariable Long id) {
        service.delete(new Permission(id,null,null));
        return id;
    }

    @GetMapping(path = "/api/permission/{id}")
    public Permission getRole(@PathVariable Long id) {
        return service.findOne(Permission.class, id);
    }

    @GetMapping(path = "/api/permission")
    public List<Permission> listRole() {
        return service.findAll(Permission.class);
    }
}
