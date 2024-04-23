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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupDetailAPI {
    private final GenericService<UserGroupDetail> service;
    private final UserService userService;

    public UserGroupDetailAPI(GenericService<UserGroupDetail> service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @PostMapping(path = "/api/userGroupDetail/add")
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

    @PostMapping(path = "/api/userGroupDetail/edit")
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

    @PostMapping(path = "/api/userGroupDetail/remove/{id}")
    public Long removeUserGroupDetail(@PathVariable Long id) {
        service.delete(id, UserGroupDetail.class);
        return id;
    }

    @GetMapping(path = "/api/userGroupDetail/{id}")
    public UserGroupDetail getUserGroupDetail(@PathVariable Long id) {
        return service.findOne(UserGroupDetail.class, id);
    }

    @GetMapping(path = "/api/userGroupDetail")
    public List<UserGroupDetail> listUserGroupDetail() {
        return service.findAll(UserGroupDetail.class);
    }
    @GetMapping(path = "/api/userGroupDetailPerUser/{userId}")
    public List<UserGroupDetail> userGroupDetails(@PathVariable Long userId) {
        return userService.findUserGroupDetail(userId);
    }

}
