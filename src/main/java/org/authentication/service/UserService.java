package org.authentication.service;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.ChangePasswordDto;
import org.authentication.model.*;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private JPA<User, Long> genericJPA;
    @Autowired
    private EntityManager entityManager;

    public void insert(User user, Long userId) throws Exception {
        user.setId(null);
        user.setInsertedUserId(userId);
        user.setInsertedDateTime(new Date());
        user.setPassword(CommonUtils.getSHA1Hash(user.getPassword()));
        genericJPA.save(user);
    }

    public void update(User user, Long userId) throws Exception {
        if (CommonUtils.isNull(user.getId()))
            throw new RuntimeException("id.not.found");
        if (CommonUtils.isNull(findOne(User.class, user.getId())))
            throw new RuntimeException("id.not.found");
        user.setUpdatedUserId(userId);
        user.setUpdatedDateTime(new Date());
        genericJPA.update(user);
    }

    public void delete(User user) {
        genericJPA.remove(user);
    }

    public int delete(Long userId) {
        return entityManager.createQuery("delete from user where user.id=" + userId).executeUpdate();
    }

    public User findOne(Class<User> aClass, Long id) {
        return genericJPA.findOne(aClass, id);
    }

    public List<User> findAll(Class<User> aClass) {
        return genericJPA.findAll(aClass);
    }

    public User findOne(String username, String password) {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        param.put("password", CommonUtils.getSHA1Hash(password));
        param.put("isActive", true);
        List<User> users = genericJPA.findAll(User.class, param);
        if (users != null && users.size() == 1)
            return users.get(0);
        return null;
    }

    public List findAll() {
        Query query = entityManager.createQuery("select o from user o");
        return genericJPA.listByQuery(query);
    }

    public List<Role> listAllRole(Long userId) {
        String hql = "select r from userRole ur \n" +
                "inner join role r on r.id=ur.role.id  \n" +
                "where ur.user.id=:userId \n" +
                "union \n" +
                "select r from userGroupDetail ugd \n" +
                "inner join userGroupRole ugr on ugr.userGroup.id = ugd.userGroup.id \n" +
                "inner join role r on r.id=ugr.role.id \n" +
                "where ugd.user.id=:userId";
        Query query = entityManager.createQuery(hql);
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public List<Permission> listAllPermission(Long userId) {
        String hql = "select p from userPermission up \n" +
                "    inner join permission p on p.id=up.permission.id \n" +
                "where up.user.id=:userId\n" +
                "union\n" +
                "select p from userRole ur \n" +
                "    inner join rolePermission rp on rp.role.id=ur.role.id\n" +
                "    inner join permission p on p.id=rp.permission.id\n" +
                "where ur.user.id=:userId\n" +
                "union\n" +
                "select p from userGroupDetail ugd\n" +
                "    inner join userGroup ug on ug.id=ugd.userGroup.id\n" +
                "    inner join userGroupRole ugr on ugr.userGroup.id=ugd.userGroup.id\n" +
                "    inner join rolePermission rp on rp.role.id=ugr.role.id\n" +
                "    inner join permission p on p.id=rp.permission.id\n" +
                "where ugd.user.id=:userId";
        Query query = entityManager.createQuery(hql);
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    @Transactional
    public int changePassword(User user, ChangePasswordDto changePasswordDto) {
        if (CommonUtils.isNull(changePasswordDto.getOldPassword()) || CommonUtils.isNull(changePasswordDto.getNewPassword()))
            throw new RuntimeException("old or new password is null");
        if (!CommonUtils.getSHA1Hash(changePasswordDto.getOldPassword()).equals(user.getPassword()))
            throw new RuntimeException("old password is incorrect");
        Map<String, Object> param = new HashMap<>();
        param.put("pass", CommonUtils.getSHA1Hash(changePasswordDto.getNewPassword()));
        param.put("userId", user.getId());
        Query query = entityManager.createQuery("update user u set u.password=:pass where u.id=:userId");
        return genericJPA.executeUpdate(query, param);
    }
    public List<UserRole> findUserRole( Long userId){
        Query query = entityManager.createQuery("select u  from userRole u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public List<UserGroupDetail> findUserGroupDetail(Long userId){
        Query query = entityManager.createQuery("select u  from userGroupDetail u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public List<UserPermission> findUserPermission(Long userId){
        Query query = entityManager.createQuery("select u  from userPermission u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }
}
