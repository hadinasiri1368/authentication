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
    public Long addUser(@RequestBody User User, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        User.setId(null);
        service.insert(User, userId);
        return User.getId();
    }

    @PostMapping(path = "/api/user/edit")
    public Long editUser(@RequestBody User User, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(User, userId);
        return User.getId();
    }

    @PostMapping(path = "/api/user/remove/{id}")
    public Long removeUser(@PathVariable Long id) {
        service.delete(new User(id, null, null, false, null));
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
