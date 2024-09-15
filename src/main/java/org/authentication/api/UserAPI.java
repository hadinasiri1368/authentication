package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.authentication.dto.RequestDto.ChangePasswordDto;
import org.authentication.dto.ResponseDto.UserPersonDto;
import org.authentication.model.Role;
import org.authentication.model.User;
import org.authentication.service.TransportServiceProxcy;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserAPI {
    private final UserService service;
    private final TransportServiceProxcy transportServiceProxcy;

    @Value("${PageRequest.page}")
    private Integer page;
    @Value("${PageRequest.size}")
    private Integer size;

    public UserAPI(TransportServiceProxcy transportServiceProxcy, UserService service) {
        this.transportServiceProxcy = transportServiceProxcy;
        this.service = service;
    }

    @PostMapping(path = "/authentication/user/add")
    public Long addUser(@RequestBody User user, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.insert(user, userId);
        return user.getId();
    }

    @PutMapping(path = "/authentication/user/edit")
    public Long editUser(@RequestBody User user, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        service.update(user, userId);
        return user.getId();
    }

    @DeleteMapping(path = "/authentication/user/remove/{id}")
    public Long removeUser(@PathVariable Long id) {
        service.delete(id);
        return id;
    }

    @GetMapping(path = "/authentication/user/{id}")
    public User getUser(@PathVariable Long id) {
        return service.findOne(User.class, id);
    }

    @GetMapping(path = "/authentication/user")
    public List<User> listUser() {
        return service.findAll(User.class);
    }

    @GetMapping(path = "/authentication/userPerson/{id}")
    public Page<UserPersonDto> listUserPersonByUserId(HttpServletRequest request, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @PathVariable(name = "id", required = false) Long id) {
        List<User> users;
        if (CommonUtils.isNull(id))
            users = service.findAll(User.class);
        else
            users = Collections.singletonList(service.findOne(User.class, id));
        String uuid = request.getHeader("X-UUID");
        List<UserPersonDto> userPersonDtos = transportServiceProxcy.getUserPerson(CommonUtils.getToken(request), uuid, users);
        if (CommonUtils.isNull(page) && CommonUtils.isNull(size)) {
            return CommonUtils.listPaging(userPersonDtos);
        }
        PageRequest pageRequest = PageRequest.of(CommonUtils.isNull(page, this.page), CommonUtils.isNull(size, this.size));
        return CommonUtils.listPaging(userPersonDtos, pageRequest);
    }

    @GetMapping(path = "/authentication/userPerson")
    public Page<UserPersonDto> listUserPerson(HttpServletRequest request, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return listUserPersonByUserId(request, page, size, null);
    }

    @GetMapping(path = "/authentication/user/role")
    public List<Role> listRole(@RequestParam(value = "userId", required = false) Long userId, HttpServletRequest request) {
        if (CommonUtils.isNull(userId))
            userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        return service.listAllRole(userId);
    }

    @PostMapping(path = "/authentication/user/changePassword")
    public int changePassword(@RequestBody ChangePasswordDto changePasswordDto, HttpServletRequest request) {
        return service.changePassword(JwtTokenUtil.getUserFromToken(CommonUtils.getToken(request)), changePasswordDto);
    }

    @GetMapping(path = "/authentication/findUser/{personId}")
    public User findPersonUser(@PathVariable Long personId) {
        return service.findUserPerson(User.class, personId);
    }

    @GetMapping(path = "/authentication/UsersPerRole/{roleId}")
    public List<User> findAllUserRole(@PathVariable Long roleId) {
        return service.findAllUserRole(roleId);
    }

}
