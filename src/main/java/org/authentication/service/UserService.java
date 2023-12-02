package org.authentication.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.authentication.common.CommonUtils;
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
        List<User> users = genericJPA.findAll(User.class, param);
        if (users != null && users.size() == 1)
            return users.get(0);
        return null;
    }

    public List findAll() {
        Query query = entityManager.createQuery("select o from user o");
        return genericJPA.listByQuery(query);
    }

}
