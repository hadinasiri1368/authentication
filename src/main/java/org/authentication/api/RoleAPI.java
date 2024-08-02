package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.Role;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class RoleAPI {
    private final GenericService<Role> service;

    public RoleAPI(GenericService<Role> service) {
        this.service = service;
    }

    @PostMapping(path = "/authentication/role/add")
    public Long addRole(@RequestBody Role role, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(role, userId);
        return role.getId();
    }

    @PutMapping(path = "/authentication/role/edit")
    public Long editRole(@RequestBody Role role, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(role, userId, Role.class);
        return role.getId();
    }

    @DeleteMapping(path = "/authentication/role/remove/{id}")
    public Long removeRole(@PathVariable Long id) {
        service.delete(id, Role.class);
        return id;
    }

    @GetMapping(path = "/authentication/role/{id}")
    public Role getRole(@PathVariable Long id) {
        return service.findOne(Role.class, id);
    }

    @GetMapping(path = "/authentication/role")
    public Page<Role> listRole(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(Role.class, page, size);
    }
}
