package org.authentication.service;

import lombok.extern.slf4j.Slf4j;
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


    public void insert(Entity entity, Long userId) {
        try {
            Method m = entity.getClass().getMethod("setId", Long.class);
            m.invoke(entity, (Long) null);
        } catch (Exception e) {
            log.error("setId has error: " + e.getMessage());
        }
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setInsertedUserId(userId);
            ((BaseEntity) entity).setInsertedDateTime(new Date());
        }
        genericJPA.save(entity);
    }

    public void update(Entity entity, Long userId) {
        if (entity instanceof BaseEntity) {
            ((BaseEntity) entity).setUpdatedUserId(userId);
            ((BaseEntity) entity).setUpdatedDateTime(new Date());
        }
        genericJPA.update(entity);
    }

    public void delete(Entity entity) {
        genericJPA.remove(entity);
    }

    public Entity findOne(Class<Entity> aClass, Long id) {
        return genericJPA.findOne(aClass, id);
    }

    public List<Entity> findAll(Class<Entity> aClass) {
        return genericJPA.findAll(aClass);
    }


}
