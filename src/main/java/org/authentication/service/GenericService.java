package org.authentication.service;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.authentication.common.CommonUtils;
import org.authentication.model.BaseEntity;
import org.authentication.repository.JPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class GenericService<Entity> {
    @Autowired
    private JPA<Entity, Long> genericJPA;

    @Autowired
    private EntityManager entityManager;


    public void insert(Entity entity, Long userId) throws Exception {
        Method m = entity.getClass().getMethod("setId", Long.class);
        m.invoke(entity, (Long) null);
        ((BaseEntity) entity).setInsertedUserId(userId);
        ((BaseEntity) entity).setInsertedDateTime(new Date());
        genericJPA.save(entity);
    }

    public void update(Entity entity, Long userId) throws Exception {
        Method m = entity.getClass().getMethod("getId");
        Long id = (Long) m.invoke(entity);
        if (CommonUtils.isNull(id))
            throw new RuntimeException("id.not.found");
        ((BaseEntity) entity).setUpdatedUserId(userId);
        ((BaseEntity) entity).setUpdatedDateTime(new Date());
        genericJPA.update(entity);
    }

    public void delete(Entity entity) {
        genericJPA.remove(entity);
    }

    public int delete(Long id, Class<Entity> aClass) {
        jakarta.persistence.Entity entity = aClass.getAnnotation(jakarta.persistence.Entity.class);
        return entityManager.createQuery("delete from " + entity.name() + " where " + entity.name() + ".id=" + id).executeUpdate();
    }

    public Entity findOne(Class<Entity> aClass, Long id) {
        return genericJPA.findOne(aClass, id);
    }

    public List<Entity> findAll(Class<Entity> aClass) {
        return genericJPA.findAll(aClass);
    }


}
