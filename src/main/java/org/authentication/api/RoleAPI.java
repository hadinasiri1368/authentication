package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.Role;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class RoleAPI {
    @Autowired
    private GenericService<Role> service;

    @PostMapping(path = "/api/role/add")
    public Long addRole(@RequestBody Role role, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(role, userId);
        return role.getId();
    }

    @PostMapping(path = "/api/role/edit")
    public Long editRole(@RequestBody Role role, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(role, userId);
        return role.getId();
    }

    @PostMapping(path = "/api/role/remove/{id}")
    public Long removeRole(@PathVariable Long id) {
        service.delete(id, Role.class);
        return id;
    }

    @GetMapping(path = "/api/role/{id}")
    public Role getRole(@PathVariable Long id) {
        return service.findOne(Role.class, id);
    }

    @GetMapping(path = "/api/role")
    public List<Role> listRole() {
        return service.findAll(Role.class);
    }
}
