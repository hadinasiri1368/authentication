package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.UserGroupDetailDto;
import org.authentication.model.User;
import org.authentication.model.UserGroup;
import org.authentication.model.UserGroupDetail;
import org.authentication.service.GenericService;
import org.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupDetailAPI {
    @Autowired
    private GenericService<UserGroupDetail> service;
    @Autowired
    private UserService userService;

    @PostMapping(path = "/authentication/userGroupDetail/add")
    public Long addUserGroupDetail(@RequestBody UserGroupDetailDto userGroupDetailDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserGroupDetail userGroupDetail = new UserGroupDetail();
        userGroupDetail.setId(userGroupDetailDto.getId());
        User user = new User();
        user.setId(userGroupDetailDto.getUserId());
        UserGroup userGroup = new UserGroup();
        userGroup.setId(userGroupDetailDto.getUserGroupId());
        userGroupDetail.setUserGroup(userGroup);
        userGroupDetail.setUser(user);
        service.insert(userGroupDetail, userId);
        return userGroupDetail.getId();
    }

    @PutMapping(path = "/authentication/userGroupDetail/edit")
    public Long editUserGroupDetail(@RequestBody UserGroupDetailDto userGroupDetailDto, HttpServletRequest request) throws Exception {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserGroupDetail userGroupDetail = new UserGroupDetail();
        userGroupDetail.setId(userGroupDetailDto.getId());
        User user = new User();
        user.setId(userGroupDetailDto.getUserId());
        UserGroup userGroup = new UserGroup();
        userGroup.setId(userGroupDetailDto.getUserGroupId());
        userGroupDetail.setUserGroup(userGroup);
        userGroupDetail.setUser(user);
        service.update(userGroupDetail, userId, UserGroupDetail.class);
        return userGroupDetail.getId();
    }

    @DeleteMapping(path = "/authentication/userGroupDetail/remove/{id}")
    public Long removeUserGroupDetail(@PathVariable Long id) {
        service.delete(id, UserGroupDetail.class);
        return id;
    }

    @GetMapping(path = "/authentication/userGroupDetail/{id}")
    public UserGroupDetail getUserGroupDetail(@PathVariable Long id) {
        return service.findOne(UserGroupDetail.class, id);
    }

    @GetMapping(path = "/authentication/userGroupDetail")
    public Page<UserGroupDetail> listUserGroupDetail(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        return service.findAll(UserGroupDetail.class, page, size);
    }

    @GetMapping(path = "/authentication/userGroupDetailPerUser/{userId}")
    public List<UserGroupDetail> userGroupDetails(@PathVariable Long userId) {
        return userService.findUserGroupDetail(userId);
    }

}
