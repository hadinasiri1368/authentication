package org.authentication.api;

import jakarta.servlet.http.HttpServletRequest;
import org.authentication.common.CommonUtils;
import org.authentication.dto.UserGroupDetailDto;
import org.authentication.model.User;
import org.authentication.model.UserGroup;
import org.authentication.model.UserGroupDetail;
import org.authentication.service.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserGroupDetailAPI {
    @Autowired
    private GenericService<UserGroupDetail> service;
    @PostMapping(path = "/api/userGroupDetai/add")
    public Long addUserGroup(@RequestBody UserGroupDetailDto userGroupDetailDto, HttpServletRequest request) {
        Long userId = CommonUtils.getUserId(CommonUtils.getToken(request));
        UserGroupDetail userGroupDetail=new UserGroupDetail();
        userGroupDetail.setId(userGroupDetailDto.getId());
        User user =new User();
        user.setId(userGroupDetailDto.getUserId());
        UserGroup userGroup=new UserGroup();
        userGroup.setId(userGroupDetailDto.getUserGroupId());
        userGroupDetail.setUserGroup(userGroup);
        userGroupDetail.setUser(user);
        service.insert(userGroupDetail, userId);
        return userGroupDetailDto.getId();
    }
}
