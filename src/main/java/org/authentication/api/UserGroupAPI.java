package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.UserGroup;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupAPI {
    private final GenericService<UserGroup> service;

    public UserGroupAPI(GenericService<UserGroup> service) {
        this.service = service;
    }

    @PostMapping(path = "/authentication/userGroup/add")
    public Long addUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request)throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(userGroup, userId);
        return userGroup.getId();
    }

    @PutMapping(path = "/authentication/userGroup/edit")
    public Long editUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request)throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(userGroup, userId, UserGroup.class);
        return userGroup.getId();
    }

    @DeleteMapping(path = "/authentication/userGroup/remove/{id}")
    public Long removeUserGroup(@PathVariable Long id) {
        service.delete(id, UserGroup.class);
        return id;
    }

    @GetMapping(path = "/authentication/userGroup/{id}")
    public UserGroup getUserGroup(@PathVariable Long id) {
        return service.findOne(UserGroup.class, id);
    }

    @GetMapping(path = "/authentication/userGroup")
    public Page<UserGroup> listUserGroup(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(UserGroup.class,page,size);
    }
}
