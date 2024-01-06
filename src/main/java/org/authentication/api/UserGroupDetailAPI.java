package org.authentication.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.UserGroupDetailDto;
import org.authentication.model.User;
import org.authentication.model.UserGroup;
import org.authentication.model.UserGroupDetail;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
public class UserGroupDetailAPI {
    @Autowired
    private GenericService<UserGroupDetail> service;

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
        service.update(userGroupDetail, userId);
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

}
