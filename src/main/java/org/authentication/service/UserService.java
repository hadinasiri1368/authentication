package org.authentication.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.authentication.common.CommonUtils;
import org.authentication.model.Permission;
import org.authentication.model.User;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private JPA<User, Long> genericJPA;
    @Autowired
    private EntityManager entityManager;

    public void insert(User user, Long userId) {
        user.setId(null);
        user.setInsertedUserId(userId);
        user.setInsertedDateTime(new Date());
        user.setPassword(CommonUtils.getSHA1Hash(user.getPassword()));
        genericJPA.save(user);
    }

    public void update(User user, Long userId) {
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
        param.put("isActive",true);
        List<User> users = genericJPA.findAll(User.class, param);
        if (users != null && users.size() == 1)
            return users.get(0);
        return null;
    }

    public List findAll() {
        Query query = entityManager.createQuery("select o from user o");
        return genericJPA.listByQuery(query);
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

}
