package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.UserGroup;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupAPI {
    @Autowired
    private GenericService<UserGroup> service;

    @PostMapping(path = "/api/userGroup/add")
    public Long addUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request)throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(userGroup, userId);
        return userGroup.getId();
    }

    @PutMapping(path = "/api/userGroup/edit")
    public Long editUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request)throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(userGroup, userId, UserGroup.class);
        return userGroup.getId();
    }

    @DeleteMapping(path = "/api/userGroup/remove/{id}")
    public Long removeUserGroup(@PathVariable Long id) {
        service.delete(id, UserGroup.class);
        return id;
    }

    @GetMapping(path = "/api/userGroup/{id}")
    public UserGroup getUserGroup(@PathVariable Long id) {
        return service.findOne(UserGroup.class, id);
    }

    @GetMapping(path = "/api/userGroup")
    public List<UserGroup> listUserGroup() {
        return service.findAll(UserGroup.class);
    }
}
