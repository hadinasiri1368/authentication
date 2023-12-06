package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.UserGroup;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserGroupAPI {
    @Autowired
    private GenericService<UserGroup> service;

    @PostMapping(path = "/api/userGroup/add")
    public Long addUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(userGroup, userId);
        return userGroup.getId();
    }

    @PostMapping(path = "/api/userGroup/edit")
    public Long editUserGroup(@RequestBody UserGroup userGroup, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(userGroup, userId);
        return userGroup.getId();
    }

    @PostMapping(path = "/api/userGroup/remove/{id}")
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
