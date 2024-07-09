package org.authentication.service;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.authentication.common.CommonUtils;
import org.authentication.dto.RequestDto.ChangePasswordDto;
import org.authentication.model.*;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private JPA<User, Long> genericJPA;
    @Autowired
    private EntityManager entityManager;
    private static List<Object[]> permissions = null;

    @Value("${PageRequest.page}")
    private Integer page;
    @Value("${PageRequest.size}")
    private Integer size;

    public void insert(User user, Long userId) throws Exception {
            user.setId(null);
            user.setInsertedUserId(userId);
            user.setInsertedDateTime(new Date());
            user.setPassword(CommonUtils.getSHA1Hash(user.getPassword()));
            genericJPA.save(user);
    }

    public void update(User user, Long userId) throws Exception {
        if (CommonUtils.isNull(user.getId()))
            throw new RuntimeException("1006");
        if (CommonUtils.isNull(findOne(User.class, user.getId())))
            throw new RuntimeException("1006");
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
        if (CommonUtils.isNull(permissions))
            permissions = listPermission();
        return permissions.stream()
                .filter(record -> userId.equals(record[1])) //user id field
                .map(record -> (Permission) record[0]) // permission field
                .collect(Collectors.toList());
    }

    public List<Permission> listAllPermission(String url) {
        if (CommonUtils.isNull(permissions))
            permissions = listPermission();
        return permissions.stream()
                .filter(record -> CommonUtils.isEqualUrl(((Permission) record[0]).getUrl().toLowerCase(),url.toLowerCase()))
                .map(record -> (Permission) record[0])
                .collect(Collectors.toList());
    }

    public void resetAllPermissions() {
        permissions = listPermission();
    }

    private List<Object[]> listPermission() {
        String hql = "select p,up.user.id userId from userPermission up \n" +
                "    inner join permission p on p.id=up.permission.id \n" +
                "union\n" +
                "select p,ur.user.id userId from userRole ur \n" +
                "    inner join rolePermission rp on rp.role.id=ur.role.id\n" +
                "    inner join permission p on p.id=rp.permission.id\n" +
                "union\n" +
                "select p,ugd.user.id userId from userGroupDetail ugd\n" +
                "    inner join userGroup ug on ug.id=ugd.userGroup.id\n" +
                "    inner join userGroupRole ugr on ugr.userGroup.id=ugd.userGroup.id\n" +
                "    inner join rolePermission rp on rp.role.id=ugr.role.id\n" +
                "    inner join permission p on p.id=rp.permission.id\n";
        Query query = entityManager.createQuery(hql);
        return genericJPA.listByQuery(query);
    }

    @Transactional
    public int changePassword(User user, ChangePasswordDto changePasswordDto) {
        if (CommonUtils.isNull(changePasswordDto.getOldPassword()) || CommonUtils.isNull(changePasswordDto.getNewPassword()))
            throw new RuntimeException("1020");
        if (!CommonUtils.getSHA1Hash(changePasswordDto.getOldPassword()).equals(user.getPassword()))
            throw new RuntimeException("1021");
        Map<String, Object> param = new HashMap<>();
        param.put("pass", CommonUtils.getSHA1Hash(changePasswordDto.getNewPassword()));
        param.put("userId", user.getId());
        Query query = entityManager.createQuery("update user u set u.password=:pass where u.id=:userId");
        return genericJPA.executeUpdate(query, param);
    }

    public List<UserRole> findUserRole(Long userId) {
        Query query = entityManager.createQuery("select u  from userRole u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public List<UserGroupDetail> findUserGroupDetail(Long userId) {
        Query query = entityManager.createQuery("select u  from userGroupDetail u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public List<UserPermission> findUserPermission(Long userId) {
        Query query = entityManager.createQuery("select u  from userPermission u where u.user.id=:userId");
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        return genericJPA.listByQuery(query, param);
    }

    public Page<User> findAll(Class<User> aClass, Integer page, Integer size) {
        if (CommonUtils.isNull(page) && CommonUtils.isNull(size)) {
            return genericJPA.findAllWithPaging(aClass);
        }
        PageRequest pageRequest = PageRequest.of(CommonUtils.isNull(page, this.page), CommonUtils.isNull(size, this.size));
        return genericJPA.findAllWithPaging(aClass, pageRequest);
    }

    public User findUserPerson(Class<User> aClass, Long personId) {
        String hql = "select u  from user u where u.personId=:personId";
        Query query = entityManager.createQuery(hql);
        Map<String, Object> param = new HashMap<>();
        param.put("personId", personId);
        return (User) genericJPA.listByQuery(query, param).get(0);
    }

}
