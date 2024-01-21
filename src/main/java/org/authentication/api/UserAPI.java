package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.authentication.dto.ChangePasswordDto;
import org.authentication.model.Role;
import org.authentication.model.User;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserAPI {
    @Autowired
    private UserService service;

    @PostMapping(path = "/api/user/add")
    public Long addUser(@RequestBody User user, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(user, userId);
        return user.getId();
    }

    @PostMapping(path = "/api/user/edit")
    public Long editUser(@RequestBody User user, HttpServletRequest request) throws Exception {
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

    @GetMapping(path = "/api/user")
    public List<User> listUser() {
        return service.findAll(User.class);
    }

    @GetMapping(path = "/api/user/role")
    public List<Role> listRole(@RequestParam(value = "userId", required = false) Long userId, HttpServletRequest request) {
        if (CommonUtils.isNull(userId))
            userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        return service.listAllRole(userId);
    }

    @PostMapping(path = "/api/user/changePassword")
    public int changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        return service.changePassword(JwtTokenUtil.getUserFromToken(CommonUtils.getToken(request)), changePasswordDto);
    }
}
