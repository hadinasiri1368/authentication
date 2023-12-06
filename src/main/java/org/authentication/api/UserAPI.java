package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.model.User;
import org.authentication.service.GenericService;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserAPI {
    @Autowired
    private UserService service;

    @PostMapping(path = "/api/user/add")
    public Long addUser(@RequestBody User user, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(user, userId);
        return user.getId();
    }

    @PostMapping(path = "/api/user/edit")
    public Long editUser(@RequestBody User user, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(user, userId);
        return user.getId();
    }

    @PostMapping(path = "/api/user/remove/{id}")
    public Long removeUser(@PathVariable Long id) {
        service.delete(id);
        return id;
    }

    @GetMapping(path = "/api/user/{id}")
    public User getUser(@PathVariable Long id) {
        return service.findOne(User.class, id);
    }

    @GetMapping(path = "/api/User")
    public List<User> listUser() {
        return service.findAll(User.class);
    }
}
