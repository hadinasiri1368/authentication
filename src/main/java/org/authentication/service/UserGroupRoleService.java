package org.authentication.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.authentication.common.CommonUtils;
import org.authentication.model.RolePermission;
import org.authentication.model.UserGroupRole;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserGroupRoleService {
    private final JPA<UserGroupRole, Long> userGroupRoleJPA;
    @PersistenceContext
    private EntityManager entityManager;

    public UserGroupRoleService(JPA<UserGroupRole, Long> genericJPA) {
        this.userGroupRoleJPA = genericJPA;
    }

    @Value("${PageRequest.page}")
    private Integer page;
    @Value("${PageRequest.size}")
    private Integer size;

    @Transactional
    public void insert(UserGroupRole userGroupRole, Long userId) throws Exception {
        userGroupRole.setId(null);
        userGroupRole.setInsertedUserId(userId);
        userGroupRole.setInsertedDateTime(new Date());
        userGroupRoleJPA.save(userGroupRole);
    }

    @Transactional
    public void update(UserGroupRole userGroupRole, Long userId) throws Exception {
        if (CommonUtils.isNull(userGroupRole.getId()))
            throw new RuntimeException("1006");
        if (CommonUtils.isNull(findOne(UserGroupRole.class, userGroupRole.getId())))
            throw new RuntimeException("1006");
        userGroupRole.setUpdatedUserId(userId);
        userGroupRole.setUpdatedDateTime(new Date());
        userGroupRoleJPA.update(userGroupRole);
    }

    @Transactional
    public void delete(UserGroupRole userGroupRole) {
        userGroupRoleJPA.remove(userGroupRole);
    }

    @Transactional
    public int delete(Long id) {
        Query query = entityManager.createQuery("delete from userGroupRole u where u.id=:id");
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return userGroupRoleJPA.executeUpdate(query,param);
    }

    public UserGroupRole findOne(Class<UserGroupRole> aClass, Long id) {
        return userGroupRoleJPA.findOne(aClass, id);
    }

    public List<UserGroupRole> findAll(Class<UserGroupRole> aClass) {
        return userGroupRoleJPA.findAll(aClass);
    }


    public Page<UserGroupRole> findAll(Class<UserGroupRole> aClass, Integer page, Integer size) {
        if (CommonUtils.isNull(page) && CommonUtils.isNull(size)) {
            return userGroupRoleJPA.findAllWithPaging(aClass);
        }
        PageRequest pageRequest = PageRequest.of(CommonUtils.isNull(page, this.page), CommonUtils.isNull(size, this.size));
        return userGroupRoleJPA.findAllWithPaging(aClass, pageRequest);
    }

    public List<UserGroupRole> findRolesUserGroup(Long userGroupId) {
        Query query = entityManager.createQuery("select u  from userGroupRole u where u.userGroup.id=:userGroupId");
        Map<String, Object> param = new HashMap<>();
        param.put("userGroupId", userGroupId);
        return userGroupRoleJPA.listByQuery(query, param);
    }
}
