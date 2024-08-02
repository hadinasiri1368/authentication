package org.authentication.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.authentication.common.CommonUtils;
import org.authentication.model.RolePermission;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RolePermissionService {

    private final JPA<RolePermission, Long> rolePermissionJPA;
    @PersistenceContext
    private EntityManager entityManager;

    public RolePermissionService(JPA<RolePermission, Long> genericJPA) {
        this.rolePermissionJPA = genericJPA;
    }

    @Value("${PageRequest.page}")
    private Integer page;
    @Value("${PageRequest.size}")
    private Integer size;

    @Transactional
    public void insert(RolePermission rolePermission, Long userId) throws Exception {
        rolePermission.setId(null);
        rolePermission.setInsertedUserId(userId);
        rolePermission.setInsertedDateTime(new Date());
        rolePermissionJPA.save(rolePermission);
    }

    @Transactional
    public void update(RolePermission rolePermission, Long userId) throws Exception {
        if (CommonUtils.isNull(rolePermission.getId()))
            throw new RuntimeException("1006");
        if (CommonUtils.isNull(findOne(RolePermission.class, rolePermission.getId())))
            throw new RuntimeException("1006");
        rolePermission.setUpdatedUserId(userId);
        rolePermission.setUpdatedDateTime(new Date());
        rolePermissionJPA.update(rolePermission);
    }

    @Transactional
    public void delete(RolePermission rolePermission) {
        rolePermissionJPA.remove(rolePermission);
    }

    @Transactional
    public int delete(Long id) {
        Query query =entityManager.createQuery("delete from rolePermission r where r.id=:id");
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        return rolePermissionJPA.executeUpdate(query,param);
    }

    public RolePermission findOne(Class<RolePermission> aClass, Long id) {
        return rolePermissionJPA.findOne(aClass, id);
    }

    public List<RolePermission> findAll(Class<RolePermission> aClass) {
        return rolePermissionJPA.findAll(aClass);
    }


    public Page<RolePermission> findAll(Class<RolePermission> aClass, Integer page, Integer size) {
        if (CommonUtils.isNull(page) && CommonUtils.isNull(size)) {
            return rolePermissionJPA.findAllWithPaging(aClass);
        }
        PageRequest pageRequest = PageRequest.of(CommonUtils.isNull(page, this.page), CommonUtils.isNull(size, this.size));
        return rolePermissionJPA.findAllWithPaging(aClass, pageRequest);
    }

    public List<RolePermission> findPermissionsRole(Long roleId) {
        Query query = entityManager.createQuery("select u  from rolePermission u where u.role.id=:roleId");
        Map<String, Object> param = new HashMap<>();
        param.put("roleId", roleId);
        return rolePermissionJPA.listByQuery(query, param);
    }
}
